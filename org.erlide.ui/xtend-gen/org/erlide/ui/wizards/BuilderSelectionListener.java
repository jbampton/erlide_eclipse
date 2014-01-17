package org.erlide.ui.wizards;

import com.google.common.base.Objects;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.erlide.engine.model.builder.BuilderTool;
import org.erlide.ui.wizards.ErlangProjectBuilderPage;
import org.erlide.ui.wizards.NewProjectData;

@SuppressWarnings("all")
public class BuilderSelectionListener implements SelectionListener {
  private final NewProjectData info;
  
  private final ErlangProjectBuilderPage page;
  
  public BuilderSelectionListener(final NewProjectData info, final ErlangProjectBuilderPage page) {
    this.info = info;
    this.page = page;
  }
  
  public void widgetDefaultSelected(final SelectionEvent e) {
  }
  
  public void widgetSelected(final SelectionEvent e) {
    Object _data = e.widget.getData();
    this.info.setBuilder(((BuilderTool) _data));
    boolean _or = false;
    BuilderTool _builder = this.info.getBuilder();
    String _name = BuilderTool.MAKE.name();
    boolean _equals = Objects.equal(_builder, _name);
    if (_equals) {
      _or = true;
    } else {
      BuilderTool _builder_1 = this.info.getBuilder();
      String _name_1 = BuilderTool.INTERNAL.name();
      boolean _equals_1 = Objects.equal(_builder_1, _name_1);
      _or = (_equals || _equals_1);
    }
    this.page.configComposite.setVisible(_or);
    BuilderTool _builder_2 = this.info.getBuilder();
    String _name_2 = BuilderTool.MAKE.name();
    boolean _equals_2 = Objects.equal(_builder_2, _name_2);
    this.page.makeConfigComposite.setVisible(_equals_2);
  }
}
