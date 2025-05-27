import Service from '@ember/service';
import { tracked } from '@glimmer/tracking';

export default class ResultHandlerService extends Service {
  @tracked results = undefined;

  set results(value) {
    if (this.results !== undefined) {
      this.results = undefined;
    }
    this.results = value;
  }

  get isStudent() {
    if (this.results === undefined) {
      return false;
    }
    return this.results.result_type === "student";
  }

  get isTeacher() {
    if (this.results === undefined) {
      return false;
    }
    return this.results.result_type === "teacher";
  }

  get isSubject() {
    if (this.results === undefined) {
      return false;
    }
    return this.results.result_type === "subject";
  }

  get isBatch() {
    if (this.results === undefined) {
      return false;
    }
    return this.results.result_type === "batch";
  }

}
