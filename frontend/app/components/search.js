import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';

export default class SearchComponent extends Component {
  @service('result_handler') service;

  @action
  async search_student_id(_) {
    let value = document.getElementById("search__student__id").value;
    value = Number.parseInt(value);

    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'rollno': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/student/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'student',
      'data' : [data]
    };

    this.service.results = results;
    console.log(this.service.results);
  }

  @action
  async search_student_name() {
    let value = document.getElementById("search__student__name").value;
    if (value.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/student/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'student',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_teacher_id() {
    let value = document.getElementById("search__teacher__id").value;
    value = Number.parseInt(value);
    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'id': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'teacher',
      'data' : [data]
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_teacher_name() {
    let value = document.getElementById("search__teacher__name").value;
    if (value.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'teacher',
      'data' : data
    };
    this.service.results = results;
    console.log(data);
  }

  @action
  async search_subject_id() {
    let value = document.getElementById("search__subject__id").value;
    value = Number.parseInt(value);
    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'id': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'subject',
      'data' : [data]
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_subject_code() {
    let value = document.getElementById("search__subject__code").value;
    if (value.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'code': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'subject',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_subject_name() {
    let value = document.getElementById("search__subject__name").value;
    if (value.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': value,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'subject',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_batch_id() {
    let value = document.getElementById("search__batch__id").value;
    value = Number.parseInt(value);
    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'batchid': [value]
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data' : [data]
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_batch_teacherid() {
    let value = document.getElementById("search__batch__teacherid").value;
    value = Number.parseInt(value);
    if (value <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'teacherid': value
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }

  @action
  async search_batch_name() {
    let value = document.getElementById("search__batch__name").value;
    if (value.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': value
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/batch/search?" + params
    );

    if (response.status != 200) {
      return;
    }

    let data = await response.json();
    let results = {
      'result_type': 'batch',
      'data' : data
    };
    this.service.results = results;
    console.log(results);
  }
}
