window.addEventListener("load", addEventListeners);

function addEventListeners() {
	var emailButton = document.querySelector(".fa-envelope");
	var emailForm = document.getElementById("email-form")
	emailButton.addEventListener("click", function() {
		toggleForm(emailForm);
	});

	var emailClose = document.getElementById("email-close");
	emailClose.addEventListener("click", function() {
		toggleForm(emailForm);
	});

	var form = emailForm.querySelector(".form");
	form.addEventListener("submit", function(event) {
		var c = confirm("Confirm to send email.");
		if (c) {
			return true;
		} else {
			event.preventDefault();
		}
		event.preventDefault();
	});
}