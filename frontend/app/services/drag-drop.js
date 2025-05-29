import Service from '@ember/service';
import { tracked } from '@glimmer/tracking';

export default class DragDropService extends Service {
  @tracked elem = {};

  setElem(val) {
    this.elem = val;
  }

  getElem() {
    return this.elem;
  }
}
