package org.ucp.gwt.widgets;

import com.google.gwt.user.client.ui.*;
/*
Auto-Completion Textbox for GWT
Copyright (C) 2006 Oliver Albers http://gwt.components.googlepages.com/

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA

*/


public class AutoCompleteTextBox extends SafeTextBox
    implements KeyboardListener, ChangeListener, ClickListener {
   
  protected ListBox choices = new ListBox();
  protected PopupPanel choicesPopup=new PopupPanel(true);
  protected CompletionItems items = new SimpleAutoCompletionItems(new String[]{});
  protected boolean visible = false;
   
  /**
   * Default Constructor
   *
   */
  public AutoCompleteTextBox()
  {
    addKeyboardListener(this);
    choices.addChangeListener(this);
    choices.addClickListener(this);
    choicesPopup.add(choices);
    choicesPopup.setStyleName("ucpgwt-AutoCompleteTextBox-ChoicesPopup");
  }

  // add selected item to textbox
  protected void complete()
  {
    if(choices.getItemCount() > 0)
    {
      this.setText(choices.getItemText(choices.getSelectedIndex()));
    }
       
    choices.clear();
    choicesPopup.hide();
  }
   
  /**
   * Returns the used CompletionItems object
   * @return CompletionItems implementation
   */
  public CompletionItems getCompletionItems()
  {
    return this.items;
  }
   
  /**
   * A mouseclick in the list of items
   */
  public void onChange(Widget arg0) {
    complete();
  }

  public void onClick(Widget arg0) {
    complete();
  }

  /**
   * Not used at all
   */
  public void onKeyDown(Widget arg0, char arg1, int arg2) {
  }

  /**
   * Not used at all
   */
  public void onKeyPress(Widget arg0, char arg1, int arg2) {
  }
 
  /**
   * A key was released, start autocompletion
   */
  public void onKeyUp(Widget arg0, char arg1, int arg2) {
    if(arg1 == KEY_DOWN)
    {
      int selectedIndex = choices.getSelectedIndex();
      selectedIndex++;
      if(selectedIndex > choices.getItemCount())
      {
        selectedIndex = 0;
      }
      choices.setSelectedIndex(selectedIndex);
           
      return;
    }
       
    if(arg1 == KEY_UP)
    {
      int selectedIndex = choices.getSelectedIndex();
      selectedIndex--;
      if(selectedIndex < 0)
      {
        selectedIndex = choices.getItemCount();
      }
      choices.setSelectedIndex(selectedIndex);
           
      return;        
    }
       
    if(arg1 == KEY_ENTER)
    {
      if(visible)
      {
        complete();
      }
           
      return;
    }
       
    if(arg1 == KEY_ESCAPE)
    {
      choices.clear();
      choicesPopup.hide();
      visible = false;
           
      return;
    }
       
    String text = this.getText();
    String[] matches = new String[]{};
    if(text.length() > 0)
    {
      matches = items.getCompletionItems(text);
    }
       
    if(matches.length > 0)
    {
      choices.clear();
           
      for(int i = 0; i < matches.length; i++)
      {
        choices.addItem((String) matches[i]);
      }
           
      // if there is only one match and it is what is in the
      // text field anyways there is no need to show autocompletion
      if(matches.length == 1 && matches[0].compareTo(text) == 0)
      {
        choicesPopup.hide();
      } else {
        choices.setSelectedIndex(0);
        choices.setVisibleItemCount(matches.length + 1);
        choicesPopup.setPopupPosition(this.getAbsoluteLeft(), this.getAbsoluteTop() + this.getOffsetHeight());
        choices.setWidth(this.getOffsetWidth() + "px");
        choicesPopup.show();
        visible = true;
      }

    } else {
      visible = false;
      choicesPopup.hide();
    }
  }
   
  /**
   * Sets an "algorithm" returning completion items
   * You can define your own way how the textbox retrieves autocompletion items
   * by implementing the CompletionItems interface and setting the according object
   * @see SimpleAutoCompletionItem
   * @param items CompletionItem implementation
   */
  public void setCompletionItems(CompletionItems items)
  {
    this.items = items;
  }
}