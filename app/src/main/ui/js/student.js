let student_result_handle = document.getElementById("result_display");
let student_id_search = document.getElementById("search__student__id");
let student_name_search = document.getElementById("search__student__name");

function debounce(func, timeout = 300) {
	let timer;
	return (...args) => {
		clearTimeout(timer);
		timer = setTimeout(() => { func.apply(this, args); }, timeout);
	};
}

const handleIdSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		rollno: search_string,
	}).toString();

	let search_results = await fetch('/app/api/student/search?' + url_params, { method: "GET" });
	if (search_results.status == 204) {
		// TODO: Implement Not found for names
	}
	let results_json = await search_results.json();

	// Reset the result div
	student_result_handle.innerHTML = '';
	student_result_handle.innerHTML = render_student_id(results_json);
}

const handleNameSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: search_string }
	).toString();

	let search_results = await fetch("/app/api/student/search?" + url_params, { method: "GET" });
	if (search_results.status == 204) {
		// TODO: Implement Not found for names
	}

	let results_json = await search_results.json();

	student_result_handle.innerHTML = '';
	results_json.map(student => {
		console.log(student);
		student_result_handle.innerHTML += render_student_id(student);
	});
}

const render_student_id = (json_data) => {
	return `
		<div class="result__element student__element">
			<h3 class="student__element__rollno">${json_data.rollNo}</h3>
			<span></span>
			<h3 class="student__element__name">${json_data.name}</h3>
		</div>
	`;
}

student_id_search.addEventListener('input', debounce(async (event) => await handleIdSearch(event), 500));
student_name_search.addEventListener('input', debounce(async (event) => await handleNameSearch(event), 500));
