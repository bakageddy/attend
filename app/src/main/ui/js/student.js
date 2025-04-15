let result = document.getElementById("result_display");
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
	result.innerHTML = '';
	result.appendChild(render_student_id(results_json));
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

	result.innerHTML = '';
	results_json.map(student => {
		console.log(student);
		result.appendChild(render_student_id(student));
	});
})

const render_student_id = (json_data) => {
	// json_data = {rollNo:, name};
	let rollNo = json_data["rollNo"];
	let name = json_data["name"];

	let element = document.createElement("div");

	let rollNoElement = document.createElement("h1");
	rollNoElement.innerText = rollNo;

	let nameElement = document.createElement("h2");
	nameElement.innerText = name;

	element.appendChild(rollNoElement);
	element.appendChild(nameElement);
	element.classList = ["result__element"];
	return element;
}
