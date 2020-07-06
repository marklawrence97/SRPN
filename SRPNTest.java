import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SRPNTest {
    SRPN srpn;

    @Before
    public void setUpObject() {
        srpn = new SRPN();
    }

    @Test
    @DisplayName("Test the addition operator when 2 digits entered")
    public void processCommandAddition() {
        srpn.processCommand("10");
        srpn.processCommand("2");
        srpn.processCommand("+");
        assertEquals(12, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the addition operator when 2 digits entered")
    public void processCommandSubtractionLong() {
        srpn.processCommand("14098");
        srpn.processCommand("483");
        srpn.processCommand("-");
        assertEquals(13615, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName(("Handle negative input"))
    public void handleNegativeInput() {
        srpn.processCommand("5");
        srpn.processCommand("-5");
        srpn.processCommand("+");
        assertEquals(0, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test raised to the power")
    public void handleRaisedtoPower() {
        srpn.processCommand("2");
        srpn.processCommand("8");
        srpn.processCommand("^");
        assertEquals(256, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test raised to the power one line")
    public void handleRaisedtoPowerOneLine() {
        srpn.processCommand("2^8");
        assertEquals(256, srpn.handleInput("=", srpn.numbers));
    }


    @Test
    @DisplayName("Test the subtraction operator when 2 digits entered")
    public void processCommandSubtraction() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("-");
        assertEquals(8, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the subtraction operator when 2 digits entered")
    public void processPrefixingManyLines() {
        srpn.processCommand("5");
        srpn.processCommand("4");
        srpn.processCommand("10");
        srpn.processCommand("4");
        srpn.processCommand("3");
        srpn.processCommand("****9");
        assertEquals(4320, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the subtraction operator when 2 digits entered")
    public void processCommandSubtractionLongAndMany() {
        srpn.processCommand("100");
        srpn.processCommand("20");
        srpn.processCommand("-50");
        srpn.processCommand("3");
        srpn.processCommand("-");
        srpn.processCommand("-");
        srpn.processCommand("-");
        assertEquals(27, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the multiplication operator when 2 digits entered")
    public void processCommandMultiplication() {
        srpn.processCommand("9");
        srpn.processCommand("4");
        srpn.processCommand("*");
        assertEquals(36, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the divide operator when 2 digits entered")
    public void processCommandDivide() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("/");
        assertEquals(3, srpn.handleInput("=", srpn.numbers));
        srpn.processCommand("11");
        srpn.processCommand("0");
        srpn.processCommand("/");
        assertEquals(0, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the Modulus operator when 2 digits entered")
    public void processCommandModulus() {
        srpn.processCommand("11");
        srpn.processCommand("3");
        srpn.processCommand("%");
        assertEquals(2, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test the multiple integers and operations")
    public void processCommandMultipleEntries() {
        srpn.processCommand("3");
        srpn.processCommand("3");
        srpn.processCommand("*");
        srpn.processCommand("4");
        srpn.processCommand("4");
        srpn.processCommand("*");
        srpn.processCommand("+");
        assertEquals(25, srpn.handleInput("=", srpn.numbers));
        srpn.processCommand("3");
        srpn.processCommand("3");
        srpn.processCommand("4");
        srpn.processCommand("4");
        srpn.processCommand("*");
        srpn.processCommand("*");
        srpn.processCommand("+");
        assertEquals(51, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Test multiple integers, multiple operations and the introduction of d")
    public void processCommandMultipleModulo() {
        srpn.processCommand("10");
        srpn.processCommand("5");
        srpn.processCommand("100");
        srpn.processCommand("34");
        srpn.processCommand("%");
        srpn.processCommand("+");
        srpn.processCommand("*");
        assertEquals(370, srpn.handleInput("=", srpn.numbers));
    }


    @Test
    @DisplayName("Test multiple integers, multiple operations and the introduction of d")
    public void processCommandMultipleWithD() {
        srpn.processCommand("1234");
        srpn.processCommand("2345");
        srpn.processCommand("3456");
        srpn.processCommand("d");
        srpn.processCommand("+");
        srpn.processCommand("d");
        srpn.processCommand("+");
        srpn.processCommand("d");
        assertEquals(7035, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle saturation when going above the maximum value for an Integer")
    public void handlePositiveSaturation() {
        srpn.processCommand("2147483647");
        srpn.processCommand("1");
        srpn.processCommand("+");
        assertEquals(2147483647, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle saturation when going above the maximum value for an Integer")
    public void handleNegativeSaturation() {
        srpn.processCommand("-2147483647");
        srpn.processCommand("1");
        srpn.processCommand("-");
        assertEquals(-2147483648, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle saturation when going above the maximum value for an Integer")
    public void handleZeroSaturation() {
        srpn.processCommand("100000");
        srpn.processCommand("0");
        srpn.processCommand("-");
        srpn.processCommand("d");
        srpn.processCommand("*");
        assertEquals(100000, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineInputs() {
        srpn.processCommand("11+1+1+d");
        assertEquals(13, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineInputsMultiplication() {
        srpn.processCommand("11+1+2*3+2*5");
        assertEquals(28, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineInputsMultiplicationLong() {
        srpn.processCommand("11+1+2*3*2*5");
        assertEquals(72, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineInputsModulo() {
        srpn.processCommand("11%5%3");
        assertEquals(1, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineInputsPower() {
        srpn.processCommand("10+2^3+5+2^2");
        assertEquals(27, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineInputs() {
        srpn.processCommand("6");
        srpn.processCommand("+2*3+8");
        assertEquals(20, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineInputsMultiplication() {
        srpn.processCommand("6");
        srpn.processCommand("*2*3+8");
        assertEquals(44, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineInputsMultiplicationLong() {
        srpn.processCommand("10");
        srpn.processCommand("5");
        srpn.processCommand("5");
        srpn.processCommand("+++7*3");
        assertEquals(41, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineNegative() {
        srpn.processCommand("10+2-3+10*2");
        assertEquals(-11, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineSpaces() {
        srpn.processCommand("1 + 1");
        assertEquals(1, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineNoSpaces() {
        srpn.processCommand("1+1");
        assertEquals(2, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineDivideByZero() {
        srpn.processCommand("10");
        srpn.processCommand("5");
        srpn.processCommand("-5");
        srpn.processCommand("+");
        srpn.processCommand("/");
        assertEquals(0, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void testHandleComment() {
        srpn.processCommand("# test # 12+5d # comment # test");
        assertEquals(17, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void testOneLinePowerWithSpaces() {
        srpn.processCommand("3 3 ^ 3 ^ 3 ^=");
        assertEquals(2147483647, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineNegativeLong() {
        srpn.processCommand("10-5-5-5-5");
        assertEquals(10, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineNegativeAndPositiveLong() {
        srpn.processCommand("10-5+5");
        assertEquals(0, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneLineNegativeAndMultiplication() {
        srpn.processCommand("10-5+5*2");
        assertEquals(-5, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneDoubleNegative() {
        srpn.processCommand("10--5");
        assertEquals(15, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleMultiOneQuadNegative() {
        srpn.processCommand("10----5");
        assertEquals(15, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleManyMiddleOperators() {
        srpn.processCommand("100");
        srpn.processCommand("50");
        srpn.processCommand("6");
        srpn.processCommand("10++*5");
        assertEquals(106, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineManyAdditions() {
        srpn.processCommand("6");
        srpn.processCommand("5");
        srpn.processCommand("10");
        srpn.processCommand("++");
        assertEquals(21, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneMultiLineLongPrefix() {
        srpn.processCommand("10");
        srpn.processCommand("5");
        srpn.processCommand("6");
        srpn.processCommand("32");
        srpn.processCommand("+++*4");
        assertEquals(149, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneBodMas() {
        srpn.processCommand("2/1-3+7*9");
        assertEquals(-64, srpn.handleInput("=", srpn.numbers));
    }


    @Test
    @DisplayName("Handle one line inputs")
    public void handleOneLineDivision() {
        srpn.processCommand("1/2/3/4/5");
        assertEquals(1, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleManySpaces() {
        srpn.processCommand("1             2         +     ");
        assertEquals(3, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleReallyLongInfix() {
        srpn.processCommand("10/5+6*21+6-51--5/6+100*6*10^5");
        assertEquals(60000082, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleLongModuloInfix() {
        srpn.processCommand("10%3+6+10");
        assertEquals(17, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleLongModuloAndPower() {
        srpn.processCommand("10+50*5%6^3");
        assertEquals(260, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleDMidInput() {
        srpn.processCommand("11+5d*3");
        assertEquals(48, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleDandEqualsMidInput() {
        srpn.processCommand("11+5+10=d*2");
        assertEquals(52, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleOnLineDivisionWeird() {
        srpn.processCommand("76/56/876");
        assertEquals(1188, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleAdditionAndSubtractionPolish() {
        srpn.processCommand("1");
        srpn.processCommand("2");
        srpn.processCommand("+");
        srpn.processCommand("3");
        srpn.processCommand("4");
        srpn.processCommand("+");
        srpn.processCommand("-");
        srpn.processCommand("5");
        srpn.processCommand("-");
        srpn.processCommand("d");
        assertEquals(-9, srpn.handleInput("=", srpn.numbers));
    }

    @Test
    @DisplayName("Handle one line inputs")
    public void handleAdjacentText() {
        srpn.processCommand("5+5test");
        assertEquals(10, srpn.handleInput("=", srpn.numbers));
    }
}