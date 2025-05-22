"use strict";

let result_batch_handle = document.getElementById("result_display");
let batchid_input = document.getElementById("search__batch__id");
let name_input = document.getElementById("search__batch__name");
let teacherid_input = document.getElementById("search__batch__teacherid");

function debounce_batch(func, timeout = 300) {
	let timer;
	return (...args) => {
		if (!timer) {
			func.apply(this, args);
		}
		clearTimeout(timer);
		timer = setTimeout(() => { timer = undefined }, timeout);
	};
}

const search_by_id = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		// TODO: handle invalid input
		return;
	}

	let url_params = new URLSearchParams({
		"batchid": search_string
	}).toString();

	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something bad happened!");
		return;
	}

	let results_json = await results.json();

	result_batch_handle.innerHTML = '';
	result_batch_handle.innerHTML = render_batch(results_json);

	document.querySelectorAll('.batch__element').forEach( element => {
		element.addEventListener("click", event => {
			let batchid = event.currentTarget.querySelector(".batch__element__batchid");
			if (!batchid) return;
			let value = Number.parseInt(batchid.textContent);
			let batch_id_element = document.getElementById("batch__input__batchid");
			batch_id_element.value = value;

			let teacherid = event.currentTarget.querySelector(".batch__element__teacherid");
			if (!teacherid) return;
			let teacherid_value = Number.parseInt(teacherid.textContent);
			let teacherid_element = document.getElementById("batch__input__teacherid");
			teacherid_element.value = teacherid_value;
		});
	});
}

const search_by_name = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		"pattern": search_string
	});

	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something erronous happened!");
		return;
	}

	let results_json = await results.json();
	result_batch_handle.innerHTML = '';

	let html = "";
	results_json.map(batch => {
		html += render_batch(batch);
	});
	result_batch_handle.innerHTML = html;

	document.querySelectorAll(".batch__element").forEach(element => {
		element.addEventListener("click", event => {
			let batchid = event.currentTarget.querySelector(".batch__element__batchid");
			if (!batchid) return;
			let value = Number.parseInt(batchid.textContent);
			let batch_id_element = document.getElementById("batch__input__batchid");
			batch_id_element.value = value;

			let teacherid = event.currentTarget.querySelector(".batch__element__teacherid");
			if (!teacherid) return;
			let teacherid_value = Number.parseInt(teacherid.textContent);
			let teacherid_element = document.getElementById("batch__input__teacherid");
			teacherid_element.value = teacherid_value;
		});
	});
}

const search_by_teacherid = async (event) => {
	let search_string = event.target.value;
	if (search_string.length === 0) {
		return;
	}

	let url_params = new URLSearchParams({
		"teacherid": search_string
	});
	let results = await fetch("/app/api/batch/search?" + url_params);
	if (results.status != 200) {
		alert("Something erronous happened!");
		return;
	}

	let results_json = await results.json();

	result_batch_handle.innerHTML = '';
	let html = "";

	results_json.map(batch => {
		html += render_batch(batch);
	});
	result_batch_handle.innerHTML = html;

	document.querySelectorAll(".batch__element").forEach(element => {
		element.addEventListener("click", event => {
			console.log("I got clicked!");
			let batchid = event.currentTarget.querySelector(".batch__element__batchid");
			if (!batchid) return;
			let value = Number.parseInt(batchid.textContent);
			let batch_id_element = document.getElementById("batch__input__batchid");
			batch_id_element.value = value;

			let teacherid = event.currentTarget.querySelector(".batch__element__teacherid");
			if (!teacherid) return;
			let teacherid_value = Number.parseInt(teacherid.textContent);
			let teacherid_element = document.getElementById("batch__input__teacherid");
			teacherid_element.value = teacherid_value;
		});
	});
}

const render_batch = (json_data) => {
	return `
		<div class="batch__element result__element">
			<div class="batch__element__batchid">${json_data.batchid}</div>
			<div class="batch__element__name">${json_data.name}</div>
			<div class="batch__element__teacherid">${json_data.teacherid}</div>
		</div>
	`;
}

batchid_input.addEventListener("input", async (event) => await search_by_id(event));
name_input.addEventListener("input", debounce_batch(async (event) => await search_by_name(event)));
teacherid_input.addEventListener("input", debounce_batch(async (event) => await search_by_teacherid(event)));
