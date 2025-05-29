import Component from "@ember/component";
import { service } from '@ember/service';
import { action } from '@ember/object';
import { tracked } from "@glimmer/tracking";

export default class SubjectComponent extends Component {
  @service('result_handler') service;

  @tracked subjectid    = undefined;
  @tracked subjectcode  = undefined;
  @tracked subjectname  = undefined;

  set subjectid(val) {
    if (this.subjectid !== undefined) {
      this.subjectid = val;
    }
  }

  set subjectcode(val) {
    if (this.subjectcode !== undefined) {
      this.subjectcode = val;
    }
  }

  set subjectname(val) {
    if (this.subjectname !== undefined) {
      this.subjectname = val;
    }
  }

  @action
  async search_subject_id(ev) {
    this.subjectid = ev.target.value;
    if (this.subjectid === undefined || this.subjectid <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'id': this.subjectid,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      // TODO: Render Error Dialog
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
  async search_subject_code(ev) {
    this.subjectcode = ev.target.value;
    if (this.subjectcode.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'code': this.subjectcode,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      // TODO: Render Error Dialog
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
  async search_subject_name(ev) {
    this.subjectname = ev.target.value;
    if (this.subjectname.length <= 0) {
      return;
    }

    let params = new URLSearchParams({
      'pattern': this.subjectname,
    }).toString();

    let response = await fetch(
      "http://localhost:8080/app/api/subject/search?" + params,
    );

    if (response.status != 200) {
      // TODO: Render Error Dialog
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
}
