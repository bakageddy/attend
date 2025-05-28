import { module, test } from 'qunit';
import { setupRenderingTest } from 'frontend/tests/helpers';
import { render } from '@ember/test-helpers';
import { hbs } from 'ember-cli-htmlbars';

module('Integration | Component | search/index', function (hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function (assert) {
    // Set any properties with this.set('myProperty', 'value');
    // Handle any actions with this.set('myAction', function(val) { ... });

    await render(hbs`<Search::Index />`);

    assert.dom().hasText('');

    // Template block usage:
    await render(hbs`
      <Search::Index>
        template block text
      </Search::Index>
    `);

    assert.dom().hasText('template block text');
  });
});
