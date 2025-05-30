import Component from "@glimmer/component";
import { service } from "@ember/service";
import { action } from "@ember/object";

export default class ErrorDialogComponent extends Component {
  @service('error_service') error;

  @action
  clearAndGoBack() {
    this.error.clear_err();
  }
}
