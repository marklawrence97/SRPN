import java.util.*;

public class SRPN {
    final public Stack<Double> numbers;
    private boolean isComment;
    private int pseudoRandomNumberIndex;

    public SRPN() {
        /*
        The constructor initialises a Stack which will keep track of the order of integers that are to be processed.
        This gives the calculator a memory so it will 'remember' what was inputted on previous lines, it also keeps
        track of whether the calculator is expecting to receive a comment.

        The key methods of this class are:

        - processCommand(), this method handles the preprocessing of input before it is added to the
        calculator.

        - handleInput(), this method processes the user input, if it is a number the number will be added to the stack which
        acts as the calculator's working memory, however if it is a special character it will perform an action on the
        stack.

        - handleOneLineInput() method preprocess input, it a string containing multiple inputs into a list of single
        inputs. The list contains strings that are in the correct format to be processed by the calculator.

        - processOperation(), this method acts as the actual calculator. It takes an input of a stack and
        an operation, It then performs the required operation on the appropriate elements of the stack. By taking a
        stack an an input it can be used by the calculator to mutate the state and then reused when handling
        one line input.
        */

        this.numbers = new Stack<>();
        this.isComment = false;
        this.pseudoRandomNumberIndex = 0;
    }

    public void processCommand(String s) {
//      This method processes the command according to the input from the user.

        if (s.equals("") || (this.isComment && !s.contains("#"))) return;

        if (s.contains("#")) s = handleComment(s);

//      The handle comment method may return an empty string, so we have to make this check again.
        if (s.equals("")) return;

//      split input string at spaces; effectively treating a space as a new line
        for (String string: s.split("\\s")) {
            for (String input: handleOneLineInput(string)) handleInput(input, this.numbers);
        }
    }

    public int handleInput(String userInput, Stack<Double> stack) {
        /*
        This method handles input. It uses regex to match with digits and operations. It also handles the special cases
        where d and r are entered.
         */

//      Return immediately if the input is empty or whitespace
        if (userInput.matches("\\s") || userInput.equals("")) {
            return 0;
        }

//      Match with input if it is a digit of any length optionally preceded by a negative sign
        if (userInput.matches("^-?\\d+")) {
            double inputNumber = handleSaturation(Double.parseDouble(userInput));
            addToStack(inputNumber, stack);
            return 0;
        }

        if (userInput.equals("d")) {
            this.printStack();
            return 0;
        }

        if (userInput.equals("r")) {
            addToStack(this.getPseudoRandomNumber(), stack);
            this.pseudoRandomNumberIndex = (this.pseudoRandomNumberIndex + 1) % 22;
            return 0;
        }

        if (userInput.matches("[*+-/%^]")) {
//          If the stack is too small to have an operation performed on it .
            if (stack.size() <= 1) {
                System.out.println("Stack underflow");
                return 0;
            }

//          If operation will result in division by zero, print "Divide by 0."
            if (stack.peek() == 0 && userInput.equals("/") && stack.size() > 1) {
                System.out.println("Divide by 0.");
                return 0;
            }

            processOperation(userInput, stack);
            return 0;
        }

        if (userInput.charAt(userInput.length()-1) == '=') {
            if (stack.isEmpty()) {
                System.out.println("Stack empty.");
                return 0;
            }

            System.out.println( (int) Math.floor(stack.peek()));
            return (int) Math.floor(stack.peek());
        }

        System.out.println("Unrecognised operator or operand \"" + userInput + "\"");
        return 0;
    }

    private List<String> handleOneLineInput(String userInput) {
        /*
        This method handles one line input from the user. It parses it into a form that can then be easily processed
        by the handleInfix method, or if it is not infix form ensures that it will be handled correctly by the
        calculator.
        */

//      Split at occurrence of operator. Only count "-" as an operator if it is not followed by a digit.
        String delimiter = "[dr+*/%=^]|-(?!\\d)";
        ArrayList<String> inputArray  = new ArrayList<>(Arrays.asList(userInput.split(String.format("((?<=%s)|(?=%s))",
                delimiter, delimiter))));

//      This cleans up the input, for example an element of our array could be 4-5 after initial split, this splits
//      it to be 4, -, 5. It handles the case invalid characters are entered. For example "test" becomes "t", "e", "s", "t"
        int numbers = inputArray.size();
        for (int i = 0; i < numbers; ++i) {
//          Split at a negative sign unless it at the start and followed by a digit.
            delimiter = "(?<!^)-(?=\\d)|-(?!\\d)";
            String splitAt = String.format("((?<=%s)|(?=%s))", delimiter, delimiter);
            ArrayList<String> values = new ArrayList<>(Arrays.asList(inputArray.get(i).split(splitAt)));
            inputArray.remove(i);
            inputArray.addAll(i, values);
            numbers += values.size() - 1;

            if (!inputArray.get(i).matches("-?\\d+|[dr+*/%=^\\-]")) {
                values = new ArrayList<>(Arrays.asList(inputArray.get(i).split("")));
                inputArray.remove(i);
                inputArray.addAll(i, values);
                numbers += values.size() - 1;
            }
        }

        return handleInfix(inputArray);
    }

    private List<String> handleInfix(ArrayList<String> infix) {
    /*
        This method handles the infix behaviour the legacy calculator produces. It takes an input of an ArrayList
        containing the input values from the user. The method returns a list containing the correct order for the
        polish calculator to process.

        This method uses a map to store key value pairs which represent the order of precedence
        an operator has, in the case the lower the value the higher the precedence.

        Then there is a loop over all the different values. If a value is not an operator it will add it to an ArrayList
        that contains the final order. If a value is an operator then it will pop all operators from the operator stack
        until the operator at the top of the stack is of equal or less precedence, then the operator will be added to
        the operator stack.

        Finally after the loop is finished if there are any operators remaining on the stack they are added to the
        end of the ArrayList.

        For Example:

            handleInput("10+2-3+10*2") -> handleInfix([10, +, 2, -, 3, +, 10, *, 2]) -> [10, 2, +, 3, 10, 2, *, +, -].

        We end up with a slightly modified version of Bodmas where precedence is respected but calculations are made
        right to left instead of left to right. This implementation is a slightly modified implementation of Dijskra's
        shunting yard algorithm, for more information see https://brilliant.org/wiki/shunting-yard-algorithm/.
     */

        Stack<String> operators = new Stack<>();
        ArrayList<String> processedOrder = new ArrayList<>();
        Map<String, Integer> precedence = new HashMap<>();
        precedence.put("^", 0);
        precedence.put("%", 1);
        precedence.put("*", 2);
        precedence.put("/", 2);
        precedence.put("+", 3);
        precedence.put("-", 4);

        for (String item: infix) {
//          If item is a digit with an optional negative sign or a r or =.
            if (item.matches("-?\\d+|[r=]")) {
                processedOrder.add(item);
            }

//          If item is an operator
            if (item.matches("[*/+\\-^%]")) {
                try {
                    while (precedence.get(operators.peek()) < precedence.get(item)) {
                        processedOrder.add(operators.pop());
                    }
                } catch (EmptyStackException e) {}
                operators.add(item);
            }

//          If item matches "d"
            if (item.matches("d")) {
                while (operators.size() > 0) {
                    processedOrder.add(operators.pop());
                }
                processedOrder.add(item);
            }

//          If item is anything else
            if (!item.matches("d|[*/+\\-^%]|-?\\d+|[r=]")) {
                processedOrder.add(item);
            }
        }

        while (operators.size() > 0) {
            processedOrder.add(operators.pop());
        }

        return processedOrder;
    }

    private static void processOperation(String operation, Stack<Double> stack) {
        /*
        This method handles the case an operation is entered. It pops the top two integers off the stack and performs
        the appropriate calculation. It then adds the result back to the stack. If a division by 0 occurs, this error
        is printed, and then both numbers are added back to the stack, effectively leaving the stack unchanged.
        */

        double a = stack.pop();
        double b = stack.pop();
        double result;

        switch (operation) {
            case "*" -> result = a * b;
            case "+" -> result = a + b;
            case "-" -> result = b - a;
            case "/" -> {
                if (a == 0) {
                    result = 0;
                    stack.add(a);
                    stack.add(b);
                    break;
                }
                result = b / a;
            }
            case "%" -> result = b % a;
            case "^", "\\^" -> result = (int) Math.pow(b, a);
            default -> throw new IllegalStateException("Unexpected value: " + operation);
        }
        stack.add(handleSaturation(result));
    }

    private static double handleSaturation(double number) {
        /*
        This helper method handles saturation. If a number is entered that is out of the range for the values of an
        integer it will either return the upper bound, or the lower bound. Else, it will simply return the number.
        This works when |number| <= 9,223,372,036,854,775,807.
        */

        if (number >= Integer.MAX_VALUE) {
            number = Integer.MAX_VALUE;
        }

        if (number <= Integer.MIN_VALUE) {
            number = Integer.MIN_VALUE;
        }

        return number;
    }

    private void printStack() {
        /*
        This method copies the Stack with the integers to be processed. It then creates a new stack in order to print
        out each integer in the correct order.
         */

        Stack<Double> copyOfNumbers = (Stack<Double>) this.numbers.clone();
        Stack<Double> numbersToPrint = new Stack<>();

        if (this.numbers.isEmpty()) {
            System.out.println(Integer.MIN_VALUE);
            return;
        }

        while (!copyOfNumbers.isEmpty()) {
            numbersToPrint.add(copyOfNumbers.pop());
        }

        while (!numbersToPrint.isEmpty()) {
            System.out.println((int) Math.floor(numbersToPrint.pop()));
        }
    }

    private static void addToStack(double toAdd, Stack<Double> stack) {
        /*
            This helper function takes an input of a stack and a double that is intended to be added to the stack.
            It then checks the size of stack and if the stack is sufficiently small it will then add the double
            to the stack, else it will simply print "Stack overflow.".
         */
        if (stack.size() > 22) {
            System.out.println("Stack overflow.");
            return;
        }

        stack.add(toAdd);
    }

    private int getPseudoRandomNumber() {
        /*
            This method takes no arguments. It uses the pseudoRandomNumberIndex property to select a pseudorandom number
            from an array.
         */

        int[] randomNumbers = new int[] {
            1804289383,
            846930886,
            1681692777,
            1714636915,
            1957747793,
            424238335,
            719885386,
            1649760492,
            596516649,
            1189641421,
            1025202362,
            1350490027,
            783368690,
            1102520059,
            2044897763,
            1967513926,
            1365180540,
            1540383426,
            304089172,
            1303455736,
            35005211,
            521595368,
        };

        return randomNumbers[this.pseudoRandomNumberIndex];
    }

    private String handleComment(String s) {
/*      This method processes a string containing a "#". It splits the string at # but retains the #, it then returns
        a string with the comments removed. To handle multi line comments it updates a boolean field which is set to
        true when part of a string will be removed.

        For example, if this.isComment = False to begin, then this is how the following string is manipulated:
        "# comment # notComment" -> ["#", " comment", "#", " notComment"] -> [" notComment"]. */

//      The delimiter we can use is a hash that is adjacent to anything but whitespace.
        String delimiter = "(?<![^ ])#(?![^ ])";
//      Using positive lookahead and lookbehind we can split at our delimiter whilst adding our delimiter to the array
        String[] comments  = s.split(String.format("((?<=%s)|(?=%s))", delimiter, delimiter));
        StringBuilder returnString = new StringBuilder();

        for (String c: comments) {
            if (c.equals("#")) this.isComment = !this.isComment;
            if (!this.isComment && !c.equals("#")) returnString.append(c);
        }

        return returnString.toString();
    }
}
