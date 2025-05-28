import { module, test } from 'qunit';
import { setupRenderingTest } from 'frontend/tests/helpers';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

module('Integration | Component | result/teacher', function (hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function (assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`<Result::Teacher />`);

    assert.dom().hasText('');

    // Template block usage:
    await render(hbs`
      <Result::Teacher>
        template block text
      </Result::Teacher>
    `);

    assert.dom().hasText('template block text');
  });
});
