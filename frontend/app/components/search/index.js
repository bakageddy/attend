import Component from "@glimmer/component";
import { tracked } from "@glimmer/tracking";
import { action } from "@ember/object";

export default class SearchComponent extends Component {
  get fields() {
    return this.fields;
  }
  @tracked current_field = "batch";

  get is_batch() {
    return this.current_field === "batch";
  }

  get is_teacher() {
    return this.current_field === "teacher";
  }

  get is_subject() {
    return this.current_field === "subject";
  }

  get is_student() {
    return this.current_field === "student";
  }

  @action
  set_field(field) {
    console.log(`Selected: ${field}`);
    this.current_field = field;
  }
}
