window.addEventListener("load",/*addFormListeners*/selectForms);

function selectForms() {
	var forms = document.querySelectorAll(".form");
	for (var i = 0; i < forms.length; i++) {
		addFormListeners(forms[i]);
	}
}

function addFormListeners(form) {
	//var form = document.querySelector(".form");

	if (form === null) {
		window.removeEventListener("load");
		return;
	}

	var inputs = form.getElementsByTagName("input");
	for (var i = 0; i < inputs.length-1; i++) {
		if ((inputs[i].classList.contains("form__input--text") || 
			inputs[i].classList.contains("form__input--password")) && 
			(inputs[i].value !== null && 
			inputs[i].value !== "")) 
			reloadToTop(inputs[i].parentNode.getElementsByTagName("label")[0]);
	}

	var counters = form.querySelectorAll(".form__counter");
	for (var i = 0; i < counters.length; i++) {
		var siblingInput = counters[i].parentNode.querySelector(".form__input--text");
		if (siblingInput.value.length > 20) {
			var counterSpan = counters[i].getElementsByTagName("span")[0];
			counterSpan.innerHTML = siblingInput.value.length;
			counterSpan.classList.add("form__counter--color-red");
		}
	}

	addFocusBlurListeners(form);

	form.addEventListener("keyup", function(event) {
		var input = event.target;
		var label = event.target.parentNode.getElementsByTagName("label")[0];
		var counter = event.target.parentNode.querySelector(".form__counter");
		if (counter === null) {
			if (input !== undefined && label !== undefined) {
				input.classList.remove("form__input--text--color-red");
				label.classList.remove("form__label--color-red");
			}
			return;
		}
		var inputLength = event.target.value.length;
		var counterSpan = counter.getElementsByTagName("span")[0];
		counterSpan.innerHTML = inputLength;
		if (inputLength > 20) {
			input.classList.add("form__input--text--color-red");
			label.classList.add("form__label--color-red");
			counterSpan.classList.add("form__counter--color-red");
		} else {
			input.classList.remove("form__input--text--color-red");
			label.classList.remove("form__label--color-red");
			counterSpan.classList.remove("form__counter--color-red");
		}
	});

	form.addEventListener("keyup", function(event) {
		inputExists(event);
	});
}

function addFocusBlurListeners(form) {
	form.addEventListener("focus", function(event) {
		var input = event.target;
		if (input.classList.contains("form__input--text") || 
			input.classList.contains("form__input--password")) {
			var label = event.target.parentNode.getElementsByTagName("label")[0];
			focusToTop(label);
		}
	}, true);

	form.addEventListener("blur", function(event) {
		var input = event.target;
		var label = event.target.parentNode.getElementsByTagName("label")[0];
		if (input.classList.contains("form__input--text") || 
			input.classList.contains("form__input--password")) {
			if (input.value === null || input.value === "")
				blurToBottom(label);
			else 
				blurToTop(label);
		}
	}, true);
}

function reloadToTop(label) {
	label.classList.add("form__label--position-top","form__label--display-toggle","form__label--color-gray");
}

function focusToTop(label) {
	label.classList.remove("form__label--color-gray");
	label.classList.add("form__label--position-top","form__label--display-toggle","form__label--color-blue");
}

function blurToBottom(label) {
	label.classList.remove("form__label--position-top","form__label--display-toggle","form__label--color-blue");
}

function blurToTop(label) {
	label.classList.remove("form__label--color-blue");
	label.classList.add("form__label--color-gray");
}

function inputExists(event) {
	var input = event.target;
	var id = input.id;
	if (id !== "username" && 
		id !== "email") {
		return;
	} else if (input.value.length > 20 && id === "username") {
		var result = document.querySelector("#"+id+"__result");
		result.className = "form__notice";
		result.innerHTML = "";
		return;
	}

	var httpRequest;	
	httpRequest = new XMLHttpRequest();

	if (!httpRequest) {
		console.log("request creation error");
		return;
	}

	var jsonUsername = {[id]:""+input.value};
	var jsonStringUsername = JSON.stringify(jsonUsername);
	httpRequest.onreadystatechange = function() { checkInputExists(input, id) };
	httpRequest.open("POST", "/_users/"+id, true);
	httpRequest.setRequestHeader("Content-type", "application/json");
	httpRequest.send(jsonStringUsername);

	function checkInputExists(input, id) {
		if (httpRequest.readyState === XMLHttpRequest.DONE) {
			if (httpRequest.status === 200) {
				var result = document.querySelector("#"+id+"__result");
				if (input.value.length > 0) {
					if (httpRequest.responseText !== "true") {
						result.classList.remove("form__notice--color-red");
						result.classList.add("form__notice--color-green");
						result.innerHTML = id+" available";
						result.classList.add("form__success");
					} else {
						result.classList.remove("form__notice--color-green");
						result.classList.add("form__notice--color-red");
						result.innerHTML = id+" exists";
						result.classList.add("form__error");
					}
				} else {
					result.className = "form__notice";
					result.innerHTML = "";
				}
			} else {
				console.log("request error");
			}
		}
	}
}