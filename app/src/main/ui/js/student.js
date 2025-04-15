import { compile } from "./handlebars";

let student_result_handle = document.getElementById("result_display");
let student_id_search = document.getElementById("search__student__id");
let student_name_search = document.getElementById("search__student__name");

student_id_search.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		rollno: search_string,
	}).toString();

	let search_results = await fetch('/app/api/student/search?' + url_params, { method: "GET" });
	let results_json = await search_results.json();

	// Reset the result div
	student_result_handle.innerHTML = '';
	student_result_handle.appendChild(render_student_id(results_json));
})

student_name_search.addEventListener('input', async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: `${search_string}%` }
	).toString();
	let search_results = await fetch("/app/api/student/search?" + url_params, { method: "GET" });
	let results_json = await search_results.json();

	student_result_handle.innerHTML = '';
	results_json.map(student => {
		console.log(student);
		result.appendChild(render_student_id(student));
	});
})

const render_student_id = (json_data) => {
	let student_template = document.getElementById("idempotent__student").innerHTML;
	let template = compile(student_template);
	return template(json_data);
}
