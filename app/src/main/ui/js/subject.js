let result_subject_handle = document.getElementById("result_display");
let subject_id_search = document.getElementById("search__subject__id");
let subject_code_search = document.getElementById("search__subject__code");
let subject_name_search = document.getElementById("search__subject__name");

const handleSubjectIdSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams({
		id: search_string
	});

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}

	let json_results = await result.json();

	result_subject_handle.innerHTML = '';
	json_results.forEach(element => {
		result_subject_handle.innerHTML += render_subject(element);
	});
	return;
}

const handleSubjectCodeSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}
	let url_params = new URLSearchParams(
		{ code: search_string }
	);

	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}
	let json_results = await result.json();

	result_subject_handle.innerHTML = '';
	json_results.forEach(element => {
		result_subject_handle.innerHTML += render_subject(element);
	});
	return;
}

const handleSubjectNameSearch = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams(
		{ pattern: search_string }
	);
	let result = await fetch("/app/api/subject/search?" + url_params.toString(), { method: "GET" });

	if (result.status == 204) {
		// TODO: Implement Not found for names
	}
	let json_results = await result.json();

	result_subject_handle.innerHTML = render_subject(json_results);
	return;
}

subject_id_search.addEventListener('input', debounce((event) => handleSubjectIdSearch(event), 500)); 
subject_code_search.addEventListener('input', debounce((event) => handleSubjectCodeSearch(event), 500));
subject_name_search.addEventListener('input', debounce((event) => handleTeacherNameSearch(event), 500));

const render_subject = (json_data) => {
	return `
		<div class="subject__element result__element">
			<h3 class="subject__element__id">${json_data.subject_id}</h3>
			<h3 class="subject__element__code">${json_data.subject_code}</h3>
			<h3 class="subject__element__name">${json_data.name}</h3>
		</div>
	`;
}
