package org.zanata.webtrans.client.presenter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;
import static org.zanata.webtrans.client.events.NavTransUnitEvent.NavigationType.FirstEntry;
import static org.zanata.webtrans.client.events.NavTransUnitEvent.NavigationType.LastEntry;
import static org.zanata.webtrans.client.events.NavTransUnitEvent.NavigationType.NextState;
import static org.zanata.webtrans.client.events.NavTransUnitEvent.NavigationType.PrevState;
import net.customware.gwt.presenter.client.EventBus;

import org.hamcrest.Matchers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.zanata.webtrans.client.events.NavTransUnitEvent;
import org.zanata.webtrans.client.events.UserConfigChangeEvent;
import org.zanata.webtrans.client.view.TransUnitNavigationDisplay;
import org.zanata.webtrans.shared.rpc.NavOption;

/**
 * @author Patrick Huang <a
 *         href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Test(groups = "unit-tests")
public class TransUnitNavigationPresenterTest {
    private TransUnitNavigationPresenter presenter;
    @Mock
    private TransUnitNavigationDisplay display;
    @Mock
    private EventBus eventBus;
    @Mock
    private TargetContentsPresenter targetContentsPresenter;
    private UserConfigHolder userConfigHolder;
    @Captor
    private ArgumentCaptor<NavTransUnitEvent> eventCaptor;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        userConfigHolder = new UserConfigHolder();
        presenter =
                new TransUnitNavigationPresenter(display, eventBus,
                        userConfigHolder, targetContentsPresenter);

        verify(display).setListener(presenter);
    }

    @Test
    public void onBind() {
        presenter.onBind();

        verify(eventBus).addHandler(UserConfigChangeEvent.TYPE, presenter);
    }

    @Test
    public void onUserConfigChange() {
        userConfigHolder.setNavOption(NavOption.UNTRANSLATED);

        presenter
                .onUserConfigChanged(UserConfigChangeEvent.EDITOR_CONFIG_CHANGE_EVENT);

        verify(display).setNavModeTooltip(NavOption.UNTRANSLATED);
    }

    @Test
    public void onGoToFirstEntry() {
        presenter.goToFirstEntry();

        verify(targetContentsPresenter).savePendingChangesIfApplicable();
        verify(eventBus).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRowType(),
                Matchers.equalTo(FirstEntry));
    }

    @Test
    public void onGoToLastEntry() {
        presenter.goToLastEntry();

        verify(targetContentsPresenter).savePendingChangesIfApplicable();
        verify(eventBus).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRowType(),
                Matchers.equalTo(LastEntry));
    }

    @Test
    public void onGoToPreviousState() {
        presenter.goToPreviousState();

        verify(targetContentsPresenter).savePendingChangesIfApplicable();
        verify(eventBus).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRowType(),
                Matchers.equalTo(PrevState));
    }

    @Test
    public void onGoToNextState() {
        presenter.goToNextState();

        verify(targetContentsPresenter).savePendingChangesIfApplicable();
        verify(eventBus).fireEvent(eventCaptor.capture());
        assertThat(eventCaptor.getValue().getRowType(),
                Matchers.equalTo(NextState));
    }
}
