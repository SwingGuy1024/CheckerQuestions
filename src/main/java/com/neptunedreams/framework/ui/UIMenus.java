package com.neptunedreams.framework.ui;

import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.FocusManager;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.text.JTextComponent;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 11/2/17
 * <p>Time: 11:43 PM
 *
 * @author Miguel Muñoz
 */
@SuppressWarnings({"HardCodedStringLiteral", "MagicCharacter"})
public enum UIMenus implements CaretListener {
  Menu();
  private final FocusManager focusManager = FocusManager.getCurrentManager();
  private @Nullable JTextComponent caretOwner = null;
  private final ClipboardAction cutAction = new ClipboardAction("Cut", 'X', JTextComponent::cut);
  private final ClipboardAction copyAction = new ClipboardAction("Copy", 'C', JTextComponent::copy);
  private final ClipboardAction pasteAction = new ClipboardAction("Paste", 'V', JTextComponent::paste);

  UIMenus() {
    PropertyChangeListener focusListener = evt -> {
      Component focusOwner = (Component) evt.getNewValue();
      //noinspection ObjectEquality
      if (focusOwner != caretOwner) {
        if (caretOwner != null) {
          caretOwner.removeCaretListener(this);
  
          // error: [dereference.of.nullable] dereference of possibly-null reference caretOwner
          final String text = caretOwner.getText();
          System.out.printf("deFocus c with %s to %s%n",
              text, (focusOwner == null) ? "None" : focusOwner.getClass().toString());
        }
        if (focusOwner instanceof JTextComponent) {
          // caretOwner can't get set to null here.
          caretOwner = (JTextComponent) focusOwner;
          caretOwner.addCaretListener(this);
  
          // error: [dereference.of.nullable] dereference of possibly-null reference caretOwner
          final String text = caretOwner.getText();
          System.out.printf("Focus c with %s%n", text);
        }
      }
    };
    focusManager.addPropertyChangeListener("permanentFocusOwner", focusListener);
  }

  private final class ClipboardAction extends AbstractAction {
    private final Consumer<JTextComponent> operation;

//    @SuppressWarnings("method.invocation")
    private ClipboardAction(final String name, 
                           final char acceleratorKey, 
                           Consumer<JTextComponent> operation) {
      super(name, null);
      final int acceleratorMaskForPlatform = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
      KeyStroke keyStroke = KeyStroke.getKeyStroke(acceleratorKey, acceleratorMaskForPlatform);
      setAccelerator(keyStroke);
      this.operation = operation;
    }

    @SuppressWarnings("method.invocation")  // call to putValue() not allowed on the given receiver.
    private void setAccelerator(@UnderInitialization(AbstractAction.class) ClipboardAction this, KeyStroke keyStroke) {
      putValue(Action.ACCELERATOR_KEY, keyStroke); // This is in a separate method to avoid annotating the whole method.
    }

    @Override
    public void actionPerformed(final ActionEvent e) {
      Component focusOwner = focusManager.getPermanentFocusOwner();
      if (focusOwner instanceof JTextComponent textComponent) {
        // It better be!
        operation.accept(textComponent);
      }
    }

    @SuppressWarnings("UseOfClone")
    @Override
    public ClipboardAction clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException("ClipboardAction");
    }
  }

  private void removeCaretListener(@NonNull JTextComponent owner) {
    owner.removeCaretListener(this);
  }
  
  private void addCaretListener(@NonNull JTextComponent owner) {
    owner.addCaretListener(this);
  }
  
  public void installMenu(JFrame frame) {
    JMenu editMenu = new JMenu("Edit");
    JMenuItem cutItem = new JMenuItem(cutAction);
    editMenu.add(cutItem);
    editMenu.add(copyAction);
    editMenu.add(pasteAction);

    JMenuBar menuBar = new JMenuBar();
    menuBar.add(editMenu);
    frame.setJMenuBar(menuBar);
  }

  @Override
  public void caretUpdate(final CaretEvent e) {
    boolean selectionPresent = e.getDot() != e.getMark();
    System.out.printf("Selection %b from %d =? %d%n", selectionPresent, e.getDot(), e.getMark());
    cutAction.setEnabled(selectionPresent);
    copyAction.setEnabled(selectionPresent);
    final Object source = e.getSource();
    if (source instanceof JTextComponent textComponent) {
      pasteAction.setEnabled(textComponent.isEnabled() && textComponent.isEditable());
    }
  }
}
