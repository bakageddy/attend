import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from "@glimmer/tracking";

export default class TeacherComponent extends Component {
  @service('result_handler') service;
  @service('error_service') error;

  @tracked teacherid    = undefined;
  @tracked teachername  = undefined;

  @action
  async search_teacher_id(ev) {
    this.teacherid = ev.target.value;
    if (this.teacherid === undefined || this.teacherid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'id': this.teacherid,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status !== 200) {
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect number)")
      } else if (response.status === 204) {
        this.error.set("No teacher found with the given ID");
      } else if (response.status === 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
      }
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
  async search_teacher_name(ev) {
    this.teachername = ev.target.value;
    if (this.teachername.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': this.teachername,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/teacher/search?" + params,
    );

    if (response.status !== 200) {
      if (response.status === 400) {
        this.error.set("Something went wrong! Try checking your search parameters (expect number)")
      } else if (response.status === 204) {
        this.error.set("No teacher found with the given ID");
      } else if (response.status === 500) {
        this.error.set("Something went wrong with the server! We are working on it!");
      }
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
}
