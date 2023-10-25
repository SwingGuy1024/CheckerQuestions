## Checker Framework Questions
This repository provides two minimal test cases for two questions about the 
Checker Framework for Java.
### Building

To build, type `mvn clean install`

This uses the checkerframework, which can be finicky. If maven will not build be sure of three things:

1. Maven should be 3.6 or later
 
1. The maven runner should use java 17, but not later versions.

1. JAVA_HOME should be defined, and point to JDK 17. 

### Question 1

Here is a sample constructor for a class called `UIMenus`:

```
1   UIMenus() {
2     PropertyChangeListener focusListener = evt -> {
3       Component focusOwner = (Component) evt.getNewValue();
4       //noinspection ObjectEquality
5       if (focusOwner != caretOwner) {
6         if (caretOwner != null) {
7           caretOwner.removeCaretListener(this); // Checker Frame knows caretOwner isn't null here.
8 
9           // error: [dereference.of.nullable] dereference of possibly-null reference caretOwner
10          final String text = caretOwner.getText();
11          System.out.printf("deFocus c with %s to %s%n",
12              text, (focusOwner == null) ? "None" : focusOwner.getClass().toString());
13        }
14        if (focusOwner instanceof JTextComponent) {
15          // focusOwner can't be null, so caretOwner can't get set to null here.
16          caretOwner = (JTextComponent) focusOwner;
17          caretOwner.addCaretListener(this);
18
19          // error: [dereference.of.nullable] dereference of possibly-null reference caretOwner
20          final String text = caretOwner.getText();
21          System.out.printf("Focus c with %s%n", text);
22        }
23      }
24    };
25    focusManager.addPropertyChangeListener("permanentFocusOwner", focusListener);
26  }
```

On lines 10 and 20, I'm getting an "dereference.of.nullable" error. But each statement is inside
an `if` block and has been tested for null. Furthermore, each has been already dereferenced on
the previous line. So on line 7, the checker framework knows that caretOwner is not null, but 
not on line 10. The same is true of lines 17 and 20. It seems to me that the null checker should
know that the value is not null for the rest of the `if` block, but that's not what I'm seeing.
Am I misunderstanding something about how the null checker works? Or is this a bug?

### Question 2

The `OverrideBug` has a single method, which uses a static constant:

```
1     private static final NumberFormat format = NumberFormat.getCurrencyInstance();
2
3     @Override
4     public Component getListCellRendererComponent(
5             JList<?> list,
6             Object value,
7             int index,
8             boolean isSelected,
9             boolean cellHasFocus
10    ) {
11        if (value instanceof Double) {
12            value = format.format((Double)value); 
13        }
14        return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
15    }
```

When I compile this, the checker framework gives me an error on line 6: 
`[override.param] Incompatible parameter type for value.`

The method I'm overriding has this signature: 

```
    public Component getListCellRendererComponent(
        JList<?> list,
        Object value,
        int index,
        boolean isSelected,
        boolean cellHasFocus)
```
As you can see, this perfectly matches the overriding method. The Checker Framework
is objecting to overriding an Object parameter with an Object parameter!

Why am I getting this error? It makes no sense at all. And what can I do about it
besides a `@SuppressWarning` annotation? I prefer to fix warnings rather than
suppressing them, if possible. I especially don't like to suppress warnings that I
don't understand, because I've learned through experience that this is a risky
practice.