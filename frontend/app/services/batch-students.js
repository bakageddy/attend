import Service from '@ember/service';
import {TrackedArray} from 'tracked-built-ins';

export default class BatchStudentsService extends Service {
  elements = new TrackedArray([]);

  getElements() {
    return this.elements;
  }

  add(element) {
    if (element) {
      this.elements.push(element);
    }
  }

  clear() {
    while (this.elements.length > 0) {
      this.elements.pop();
    }
  }
}
