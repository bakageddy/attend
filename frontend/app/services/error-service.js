import Service, { service } from '@ember/service';
import { tracked } from '@glimmer/tracking';
import { action } from '@ember/object';

export default class ErrorServiceService extends Service {
  @service router;

  @tracked error_message = null;
  @tracked from = '';

  set(msg) {
    // this.error_message = null;
    this.from = this.router.currentRouteName;
    this.error_message = msg;
  }

  get error() {
    return this.error_message;
  }

  set from(route) {
    this.from = route;
  }

  get from() {
    return this.from;
  }

  @action
  clear_err() {
    this.error_message = null;
    this.router.transitionTo(this.from);
  }
}
