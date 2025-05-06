"use strict";

let studentid_input_element = document.getElementById("batch__data__studentid__search");
let studentname_input_element = document.getElementById("batch__data__studentname__search");
let student_search_result_container = document.getElementById("search__results");

let dragged_element = null;

const handleStudentIdInput = async event => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		rollno: search_string
	}).toString();

	let search_results = await fetch("/app/api/student/search?" + url_params, {METHOD: "get"});
	if (search_results.status === 400) {
		alert("Server says: Bad Request");
		return;
	}

	let results_json = await search_results.json();
	student_search_result_container.innerHTML = render_student(results_json);

	document.querySelectorAll('.student__element').forEach(element => {
		element.addEventListener('dragstart', event => handleDrag(event));
	});
}

const handleStudentNameInput = async event => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		pattern: search_string
	}).toString();

	let search_results = await fetch("/app/api/student/search?" + url_params, {METHOD: "get"});
	if (search_results.status === 400) {
		alert("Server says: Bad Request");
		return;
	}

	let results_json = await search_results.json();

	let html = "";
	results_json.map(element => {
		html += render_student(element);
	});
	student_search_result_container.innerHTML = html;

	document.querySelectorAll('.student__element').forEach(element => {
		element.addEventListener('dragstart', event => handleDrag(event));
	});
}

const handleDrag = event => {
	dragged_element = event.target;
}

const handleDragOver = event => {
	event.preventDefault();
}

const handleDrop = event => {
	if (event.target.id === "student_dropzone") {
		dragged_element.parentNode.removeChild(dragged_element);
		event.target.appendChild(dragged_element);
	}
}

const render_student = (json_data) => {
	return `
		<div class="result__element student__element" draggable="true">
			<div class="student__element__rollno">${json_data.rollNo}</div>
			<div class="student__element__name">${json_data.name}</div>
		</div>
	`;
}

document.getElementById("student_dropzone").addEventListener("dragover", handleDragOver);
document.getElementById("student_dropzone").addEventListener("drop", handleDrop);

studentid_input_element.addEventListener('input', event => debounce(handleStudentIdInput(event)));
studentname_input_element.addEventListener('input', event => debounce(handleStudentNameInput(event)));
