import Service from '@ember/service';
import {TrackedArray} from 'tracked-built-ins';

export default class BatchStudentsService extends Service {
  elements = new TrackedArray([]);

  getElements() {
    return this.elements;
  }

  removeDuplicates() {
    let i = 0;
    let length = this.elements.length;
    let filtered = this.elements
      .sort()
      .filter((val, idx, arr) => {
        if (idx < length - 1) {
          arr.at(idx + 1) != val;
        }
      });

    this.elements = TrackedArray.from(filtered);
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
