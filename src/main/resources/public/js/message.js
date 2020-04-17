window.addEventListener("load",addListeners);

var openObject = null;

function addListeners() {
	var newGroupMessageForm = document.querySelector(".message__form");

	// open group creation form
	var toolbar = document.querySelector(".message__toolbar");
	toolbar.addEventListener("click",function(event) {
		if (event.target.id == "toolbar-group") {
			toggleForm(newGroupMessageForm);
		}
	});

	// open form when toolbar is focused and enter is pressed
	toolbar.addEventListener("keydown", function(event) {
		if (event.target.id === "toolbar-group" && event.keyCode === 13) {
			toggleForm(newGroupMessageForm);
		}
	});

	// close form when x is clicked
	var closeForm = document.getElementById("new-group-close");
	closeForm.addEventListener("click", function() {
		toggleForm(newGroupMessageForm);
	});

	// close form when x is focused and enter is pressed
	closeForm.addEventListener("keydown", function(event) {
		if (event.keyCode === 13) {
			toggleForm(newGroupMessageForm);
		}
	});

	// close popup when background mask is clicked
	/*window.addEventListener("click", function(event) {
		var target = event.target;
		if (target.classList.contains("mask")) {
			toggleForm(openObject);
		}
	});*/

	// load messages when group is clicked
	var messageView = document.querySelector(".message__view__list");
	var messageList = document.querySelector(".message__list");
	var current;
	messageList.addEventListener("click", function(event) {
		var target = event.target;
		if (target === event.currentTarget) {
			return;
		}
		var message = target;
		if (target.classList.contains("message")) {
			message = target;
		} else {
			while (target !== event.currentTarget) {
				if (target.classList.contains("message")) {
					message = target;
					break;
				}
				target = target.parentNode;
			}
		}

		if (message === current) {
			return;
		}

		if (current !== undefined) 
			current.classList.remove("message__selected");
		message.classList.add("message__selected");
		var unread = message.querySelector(".message__unread");
		if (unread !== null) 
			unread.style.display = "none";
		current = message;
		loadChat(message, messageView);
	});

	// reload messages when new message is submitted
	var newMessageForm = document.querySelector(".message__view").querySelector(".form");
	newMessageForm.addEventListener("submit", function() {
		event.preventDefault();
		submitMessage(this, messageView);		
	}, true);

	// submit message when enter is clicked
	// do nothing if shift is held
	var textarea = document.getElementById("new-message");
	var keymap = {};
	textarea.addEventListener("keyup", function(event) {
		if (!keymap[16] && keymap[13]) {
			if (textarea.value.trim() === "") {
				keymap[event.keyCode] = event.type == "keydown";
				return;
			}
			submitMessage(newMessageForm, messageView);
		}
		keymap[event.keyCode] = event.type == "keydown";
	});

	textarea.addEventListener("keydown", function(event) {
		keymap[event.keyCode] = event.type == "keydown";
	});

	//check for usernames as characters are typed
	var memberSelector = document.getElementById("member-selector");
	var dropdown = memberSelector.parentNode.querySelector(".form__dropdown__list");
	var members = document.getElementById("members");
	var memberList = {};
	memberSelector.addEventListener("keyup", function(event) {
		var dropdownTarget = document.querySelector(".form__dropdown__target");
		if (event.keyCode === 13) {
			if (this.value.trim() !== "" && dropdownTarget !== null) {
				addMemberToList(dropdownTarget,memberSelector,dropdown,members,memberList);
			}
		} else if (event.keyCode == 38) {
			var dropdownSibling = dropdownTarget.previousSibling;

			if (dropdownSibling !== null) {
				dropdownTarget.classList.remove("form__dropdown__target");
				dropdownSibling.classList.add("form__dropdown__target");
				dropdown.scrollTop = dropdown.scrollTop - dropdownSibling.clientHeight < 0 ? 
									 0 : 
									 dropdown.scrollTop - dropdownSibling.clientHeight;
			}
		} else if (event.keyCode === 40) {
			var dropdownSibling = dropdownTarget.nextSibling;

			if (dropdownSibling !== null) {
				dropdownTarget.classList.remove("form__dropdown__target");
				dropdownSibling.classList.add("form__dropdown__target");
				dropdown.scrollTop += dropdownSibling.clientHeight;
			}		
		} else {
			userExists(event, memberList);
		}
	});

	memberSelector.addEventListener("keydown", function(event) {
		if (event.keyCode === 13) {
			event.preventDefault();
		}
	});

	// add users to the list when clicked
	dropdown.addEventListener("click", function(event) {
		addMemberToList(event.target, memberSelector, dropdown, members, memberList);
	});

	// add user to list when enter key is pressed
	dropdown.addEventListener("keyup", function(event) {
		if (event.keyCode === 13) {
			addMemberToList(event.target, memberSelector, dropdown, members, memberList);
		}
	});

	// remove selected users on click
	members.addEventListener("click", function(event) {
		var target = event.target;
		if (!target.classList.contains("form__member__close")) {
			return;
		}
		var removeMember = target.parentNode;
		memberList[removeMember.id] = "removed";

		var removeMemberParent = removeMember.parentNode;
		removeMemberParent.removeChild(removeMember);
	});

	// light blue border added on dropdown when input field is hovered over
	memberSelector.addEventListener("mouseover", function() {
		if (document.activeElement !== this && this.value.trim() !== "") {
			dropdown.classList.add("form__dropdown__list--border-light-blue");
		}
	});

	// light blue border removed on dropdown when input field is hovered over
	memberSelector.addEventListener("mouseout", function() {
		dropdown.classList.remove("form__dropdown__list--border-light-blue");
	});

	memberSelector.addEventListener("focus", function() {
		dropdown.classList.remove("form__dropdown__list--border-gray");
		dropdown.classList.remove("form__dropdown__list--border-light-blue");
		if (this.value.trim() !== "" && dropdown.children.length > 0) {
			dropdown.classList.remove("display-none");
			this.classList.add("form__selector");
		} else {
			dropdown.classList.add("display-none");
			this.classList.remove("form__selector");
		}
	});

	// add gray border to user list on blur
	memberSelector.addEventListener("blur", function() {
		dropdown.classList.add("form__dropdown__list--border-gray");
	});

	// hide dropdown on click outside the member selector input
	newGroupMessageForm.addEventListener("click", function(event) {
		if (event.target.id !== "member-selector") {
			dropdown.classList.add("display-none");
			memberSelector.classList.remove("form__selector");
		}
	});

}

function addMemberToList(target, memberSelector, dropdown, members, memberList) {
	if ((memberList[target.id] === undefined || memberList[target.id] === "removed")) {
		memberList[target.id] = "added";
	} else {
		return;
	}

	var formMember = document.createElement("div");
	formMember.classList.add("form__member");
	formMember.id = target.id;

	var formMemberButton = document.createElement("div");
	formMemberButton.classList.add("form__member__button");
	var formMemberButtonText = document.createTextNode(target.id);
	formMemberButton.appendChild(formMemberButtonText);

	var formMemberClose = document.createElement("div");
	formMemberClose.classList.add("form__member__close");
	var formMemberCloseText = document.createTextNode(" × ");
	formMemberClose.appendChild(formMemberCloseText);

	var hiddenInput = document.createElement("input");
	hiddenInput.type = "hidden";
	hiddenInput.name = "members";
	hiddenInput.value = target.id;

	formMember.appendChild(formMemberButton);
	formMember.appendChild(formMemberClose);
	formMember.appendChild(hiddenInput);

	members.appendChild(formMember);
	memberSelector.value = "";
	dropdown.classList.add("display-none");
	memberSelector.classList.remove("form__selector");
}

// send new message
function submitMessage(form, view) {
	var httpRequest;	
	httpRequest = new XMLHttpRequest();

	var formMessage = document.getElementById("new-message");
	var jsonMessage = {"message":formMessage.value};
	var jsonStringMessage = JSON.stringify(jsonMessage);

	httpRequest.onreadystatechange = getMessages;
	httpRequest.open("POST", form.action, true);
	httpRequest.setRequestHeader("Content-type", "application/json");
	httpRequest.send(jsonStringMessage);

	function getMessages() {
		if (httpRequest.readyState === XMLHttpRequest.DONE) {
			if (httpRequest.status === 200) {
				loadChat(form,view);
				updateInput();
			} else {
				console.log("request error");
			}
		}
	}

	function updateInput() {
		formMessage.value = "";
	}
}

// load chat and apply listeners to newly loaded elements
function loadChat(target, view) {
	var httpRequest;	
	httpRequest = new XMLHttpRequest();
	httpRequest.onreadystatechange = getMessages;
	httpRequest.open("GET", "/_messages/"+target.id, true);
	httpRequest.setRequestHeader("Content-type", "application/json");
	httpRequest.send();

	function getMessages() {
		if (httpRequest.readyState === XMLHttpRequest.DONE) {
			if (httpRequest.status === 200) {
				view.innerHTML = httpRequest.responseText;
				updateRequest();
			} else {
				console.log("request error");
			}
		}
	}

	function updateRequest() {
		var form = view.parentNode.querySelector(".form");
		form.action = "/messages/"+target.id;
		form.id = target.id;
		var formInput = form.querySelector(".form__input--textarea");
		formInput.disabled = "";
		addFocusBlurListeners(formInput);
		var formSubmit = form.querySelector(".form__input--submit");
		formSubmit.disabled = "";
		
		var messages = view.querySelector(".message__list");
		messages.scrollTop = messages.scrollHeight;

		var group = view.querySelector("#group");
		var groupList = view.querySelector(".group__users");
		group.addEventListener("click",function() {
			toggleForm(groupList);
		});

		var closeUserGroup = document.getElementById("user-group-close");
		closeUserGroup.addEventListener("click", function() {
			toggleForm(groupList);
		});

		closeUserGroup.addEventListener("keydown", function(event) {
			if (event.keyCode === 13) {
				toggleForm(groupList);
			}
		});		
	}
}

function userExists(event, memberList) {
	var input = event.target;
	if (input.value.trim() === "") {
		var dropdown = input.parentNode.querySelector(".form__dropdown__list");
		dropdown.classList.add("display-none");
		input.classList.remove("form__selector");
		return;
	}

	var httpRequest;	
	httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		console.log("request creation error");
		return;
	}

	var jsonUsername = {"username":""+input.value};
	var jsonStringUsername = JSON.stringify(jsonUsername);
	httpRequest.onreadystatechange = function() { getUsers(input) };
	httpRequest.open("POST", '/_messages/users', true);
	httpRequest.setRequestHeader("Content-type", "application/json");
	httpRequest.send(jsonStringUsername);

	function getUsers(input) {
		if (httpRequest.readyState === XMLHttpRequest.DONE) {
			if (httpRequest.status === 200) {
				var dropdown = input.parentNode.querySelector(".form__dropdown__list");
				if (input.value.length > 0 && httpRequest.responseText.length > 0) {
					var jsonResponse = JSON.parse(httpRequest.responseText);
					dropdown.innerHTML = "";
					dropdown.scrollTop = 0;
					var setFirstChild = false;
					var dropdownList = "";
					for (var i = 0; i < jsonResponse.length; i++) {
						if (memberList[jsonResponse[i]["username"]] !== "added") {
							if (!setFirstChild) {
								dropdownList += "<li class=\"form__dropdown__target\" id=\""+jsonResponse[i]["username"]+"\">"+jsonResponse[i]["username"]+"</li>";
								setFirstChild = true;
							} else { 
								dropdownList += "<li id=\""+jsonResponse[i]["username"]+"\">"+jsonResponse[i]["username"]+"</li>";
							}
						}
					}
					dropdown.innerHTML = dropdownList;
					if (dropdown.children.length > 0) {
						input.classList.add("form__selector");
						dropdown.classList.remove("display-none");
					} else {
						input.classList.remove("form__selector");
						dropdown.classList.add("display-none");
					}
				} else {
					dropdown.classList.add("display-none");
					input.classList.remove("form__selector");
				}
			} else {
				console.log("request error");
			}
		}
	}
}

// Toggle pop ups
/*function toggleForm(target) {
	var mask = document.querySelector(".mask");
	if (target.classList.contains("display-none")) {
		openObject = target;
		displayform(target, mask);
		window.setTimeout(function() { openForm(target); }, 0);
	} else {
		openObject = null;
		closeForm(target);
		window.setTimeout(function() { hideForm(target, mask); }, 300);
	}
}

function openForm(target) {
	target.classList.remove("opacity-zero");
}

function closeForm(target) {
	target.classList.add("opacity-zero");
}

function displayform(target, mask) {
	target.classList.remove("display-none");
	mask.classList.remove("display-none");
}

function hideForm(target, mask) {
	target.classList.add("display-none");
	mask.classList.add("display-none");
}*/