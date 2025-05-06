"use strict";
let batchid_input_element = document.getElementById("batch__data__batchid__search");
let batchname_input_element = document.getElementById("batch__data__batchname__search");
let batch_search_result_container = document.getElementById("search__results");

let batchid_scratchpad_input = document.getElementById("batch__data__scratchpad__batchid");
let teacherid_scratchpad_input = document.getElementById("batch__data__scratchpad__teacherid");

function debounce(func, timeout = 300) {
	let timer;
	return (...args) => {
		if (!timer) {
			func.apply(this, args);
		}
		clearTimeout(timer);
		timer = setTimeout(() => { timer = undefined }, timeout)
	}
}

const handleClick = event => {
	let batchid_element = event.currentTarget.querySelector('.batch__element__batchid');
	if (!batchid_element) {
		return;
	}
	let teacherid_element = event.currentTarget.querySelector('.batch__element__teacherid');
	if (!teacherid_element) {
		return;
	}
	
	batchid_scratchpad_input.value = Number.parseInt(batchid_element.textContent);
	teacherid_scratchpad_input.value = Number.parseInt(teacherid_element.textContent);
}

const handleBatchIdInput = async event => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		batchid: search_string,
	}).toString();

	let search_results = await fetch("/app/api/batch/search?" + url_params, {method: "GET"});
	if (search_results.status === 400) {
		alert("Server says: Bad Request");
		return;
	}

	let results_json = await search_results.json();
	batch_search_result_container.innerHTML =  render_batch(results_json);

	document.querySelectorAll('.batch__element').forEach(element => {
		element.addEventListener('click', event => handleClick(event));
	});
}

const handleBatchNameInput = async event => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		pattern: search_string,
	}).toString();
	let search_results = await fetch("/app/api/batch/search?" + url_params, {method: "GET"});
	if (search_results.status === 400) {
		alert("Server says: Bad Request");
		return;
	}

	let results_json = await search_results.json();

	let html = "";
	results_json.map(element => {
		html += render_batch(element);
	});

	batch_search_result_container.innerHTML = html;
	document.querySelectorAll('.batch__element').forEach(element => {
		element.addEventListener('click', event => handleClick(event));
	});
}

const render_batch = (json_data) => {
	return `
		<div class="batch__element result__element" draggable="false">
			<div class="batch__element__batchid">${json_data.batchid}</div>
			<div class="batch__element__name">${json_data.name}</div>
			<div class="batch__element__teacherid">${json_data.teacherid}</div>
		</div>
	`;
}

batchid_input_element.addEventListener('input', (event) => debounce(handleBatchIdInput(event)));
batchname_input_element.addEventListener('input', debounce(async event => handleBatchNameInput(event)));
