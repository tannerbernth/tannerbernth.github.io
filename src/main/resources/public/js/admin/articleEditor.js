const textStyle = {
	bold: "bold",
	italic: "italic",
	underline: "underline",
	subscript: "subscript",
	superscript: "superscript",
	insertOrderedList: "insertOrderedList",
	insertUnorderedList: "insertUnorderedList",
	undo: "undo",
	redo: "redo"
};

window.addEventListener("load", addArticleEditorListeners);

function addArticleEditorListeners() {
	document.execCommand("defaultParagraphSeparator", false, "p");

	var styles = Object.keys(textStyle);
	for (var i = 0; i < styles.length; i++) {
		var element = document.getElementById(styles[i]);
		element.addEventListener("mousedown", function(event) {
			event.preventDefault();
			replaceSelectedText(textStyle[this.id]);
		});
	}

	var publishArticle = document.getElementById("publish");
	publishArticle.addEventListener("click", function() {
		var editor = document.querySelector(".editor");
		var editorValue = document.getElementById("editor-value");
		editor.innerHTML.replace("<br>","");
		editorValue.value = editor.innerHTML;
	});

}

function replaceSelectedText(style) {
	document.execCommand("styleWithCss", null,true);
	document.execCommand(style, false, '');
}