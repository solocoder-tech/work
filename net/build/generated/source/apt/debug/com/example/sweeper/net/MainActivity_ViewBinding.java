// Generated code from Butter Knife. Do not modify!
package com.example.sweeper.net;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.Button;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding implements Unbinder {
  private MainActivity target;

  private View view2131230741;

  private View view2131230742;

  private View view2131230743;

  private View view2131230744;

  private View view2131230745;

  private View view2131230746;

  @UiThread
  public MainActivity_ViewBinding(MainActivity target) {
    this(target, target.getWindow().getDecorView());
  }

  @UiThread
  public MainActivity_ViewBinding(final MainActivity target, View source) {
    this.target = target;

    View view;
    view = Utils.findRequiredView(source, R.id.syn_get, "field 'synGet' and method 'onViewClicked'");
    target.synGet = Utils.castView(view, R.id.syn_get, "field 'synGet'", Button.class);
    view2131230741 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.syn_post, "field 'synPost' and method 'onViewClicked'");
    target.synPost = Utils.castView(view, R.id.syn_post, "field 'synPost'", Button.class);
    view2131230742 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.syn_post_form, "field 'synPostForm' and method 'onViewClicked'");
    target.synPostForm = Utils.castView(view, R.id.syn_post_form, "field 'synPostForm'", Button.class);
    view2131230743 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.asyn_get, "field 'asynGet' and method 'onViewClicked'");
    target.asynGet = Utils.castView(view, R.id.asyn_get, "field 'asynGet'", Button.class);
    view2131230744 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.asyn_post, "field 'asynPost' and method 'onViewClicked'");
    target.asynPost = Utils.castView(view, R.id.asyn_post, "field 'asynPost'", Button.class);
    view2131230745 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
    view = Utils.findRequiredView(source, R.id.asyn_post_form, "field 'asynPostForm' and method 'onViewClicked'");
    target.asynPostForm = Utils.castView(view, R.id.asyn_post_form, "field 'asynPostForm'", Button.class);
    view2131230746 = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.onViewClicked(p0);
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    MainActivity target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.synGet = null;
    target.synPost = null;
    target.synPostForm = null;
    target.asynGet = null;
    target.asynPost = null;
    target.asynPostForm = null;

    view2131230741.setOnClickListener(null);
    view2131230741 = null;
    view2131230742.setOnClickListener(null);
    view2131230742 = null;
    view2131230743.setOnClickListener(null);
    view2131230743 = null;
    view2131230744.setOnClickListener(null);
    view2131230744 = null;
    view2131230745.setOnClickListener(null);
    view2131230745 = null;
    view2131230746.setOnClickListener(null);
    view2131230746 = null;
  }
}
