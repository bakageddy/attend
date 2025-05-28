import Component from '@glimmer/component';
import { service } from '@ember/service';

export default class ResultComponent extends Component {
  @service('result_handler') service;
}
