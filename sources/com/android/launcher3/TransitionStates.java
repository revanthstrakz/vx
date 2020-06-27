package com.android.launcher3;

import com.android.launcher3.Workspace.State;

/* compiled from: WorkspaceStateTransitionAnimation */
class TransitionStates {
    final boolean allAppsToWorkspace;
    final boolean oldStateIsNormal;
    final boolean oldStateIsNormalHidden;
    final boolean oldStateIsOverview;
    final boolean oldStateIsOverviewHidden;
    final boolean oldStateIsSpringLoaded;
    final boolean overviewToAllApps;
    final boolean overviewToWorkspace;
    final boolean stateIsNormal;
    final boolean stateIsNormalHidden;
    final boolean stateIsOverview;
    final boolean stateIsOverviewHidden;
    final boolean stateIsSpringLoaded;
    final boolean workspaceToAllApps;
    final boolean workspaceToOverview;

    public TransitionStates(State state, State state2) {
        boolean z = false;
        this.oldStateIsNormal = state == State.NORMAL;
        this.oldStateIsSpringLoaded = state == State.SPRING_LOADED;
        this.oldStateIsNormalHidden = state == State.NORMAL_HIDDEN;
        this.oldStateIsOverviewHidden = state == State.OVERVIEW_HIDDEN;
        this.oldStateIsOverview = state == State.OVERVIEW;
        this.stateIsNormal = state2 == State.NORMAL;
        this.stateIsSpringLoaded = state2 == State.SPRING_LOADED;
        this.stateIsNormalHidden = state2 == State.NORMAL_HIDDEN;
        this.stateIsOverviewHidden = state2 == State.OVERVIEW_HIDDEN;
        this.stateIsOverview = state2 == State.OVERVIEW;
        this.workspaceToOverview = this.oldStateIsNormal && this.stateIsOverview;
        this.workspaceToAllApps = this.oldStateIsNormal && this.stateIsNormalHidden;
        this.overviewToWorkspace = this.oldStateIsOverview && this.stateIsNormal;
        this.overviewToAllApps = this.oldStateIsOverview && this.stateIsOverviewHidden;
        if (this.oldStateIsNormalHidden && this.stateIsNormal) {
            z = true;
        }
        this.allAppsToWorkspace = z;
    }
}
