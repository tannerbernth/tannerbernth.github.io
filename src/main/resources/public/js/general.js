window.addEventListener("load", addGeneralEventListeners);

function addGeneralEventListeners() {
	var navigation = document.querySelectorAll(".nav__list");
	for (var i = 0; i < navigation.length; i++) {
		navigation[i].addEventListener("keydown", function(event) {
			clickTarget(event);
		});
	}

	function clickTarget(event) {
		if (event.keyCode === 13) {
			var target = event.target;
			if (target.tagName.toLowerCase() === "div")
				return 
			else {
				if (target.hasChildNodes()) {
					target.childNodes[0].click();
				} else {
					target.click();
				}
			}
		}
	}

	var top = document.querySelector(".button__to-top");
	top.addEventListener("click", toTop);
	top.addEventListener("keydown", function(event) {
		if (event.keyCode === 13) {
			toTop();
		}
	});
	function toTop() {
		var timer = setInterval(function() {
			window.scrollBy(0,-document.body.scrollHeight/10);
			if (window.pageYOffset <= 0) {
				clearInterval(timer);
			}
		}, 10);
	}

	window.addEventListener("click", function(event) {
		var target = event.target;
		if (target.classList.contains("mask")) {
			toggleForm(openObject);
		}
	});
}

function toggleForm(target) {
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
}