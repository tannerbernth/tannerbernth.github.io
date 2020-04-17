window.addEventListener("load",addNavListeners);

function addNavListeners() {

	var menu = document.querySelector(".nav__dropdown__selector");
	menu.addEventListener("click", toggleMenu);
	menu.addEventListener("keydown", function(event) {
		var target = event.target;
		if (event.keyCode === 13) {
			if (target.id === "dropdown") 
				toggleMenu();
			else
				target.childNodes[1].click();
		}
	});
	var isOpen = false;

	function openMenu(nav) {
		nav.style.display = "flex";
		isOpen = true;
	}

	function closeMenu(nav) {
		nav.style.display = "none";
		isOpen = false;
	}

	function toggleMenu() {
		var nav = document.querySelector(".nav__dropdown");
		if (isOpen) {
			closeMenu(nav);
		} else {
			openMenu(nav);
		}
	}

	window.addEventListener("click", closeMenuOnClick);
	function closeMenuOnClick(e) {
		if (isOpen) {
			var target = (e && e.target) || (event && event.srcElement);
			var nav = document.querySelector(".nav__dropdown");
			while (target.parentNode) {
				if (target == menu)
					return;
				target = target.parentNode;
			}

			closeMenu(nav);
		}
	}

	window.addEventListener("resize", closeOnResize);
	function closeOnResize() {
		var nav = document.querySelector(".nav__dropdown");
		if (isOpen) {
			closeMenu(nav);
		}
	}

}