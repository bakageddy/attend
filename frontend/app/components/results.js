import Component from '@glimmer/component';
import { service } from '@ember/service';

export default class ResultsComponent extends Component {
  @service('result_handler') service;
}
