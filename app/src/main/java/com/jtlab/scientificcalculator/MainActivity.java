package com.jtlab.scientificcalculator;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText display = null;
    Button second = null;

    Button sin = null;
    Button cos = null;
    Button tan = null;

    Button divide = null;
    Button multiply = null;
    Button plus = null;
    Button minus = null;

    TabHost TabHostWindow = null;

    String expression = "";

    MathEval evaluator = new MathEval();

    boolean second_variant = false;
    boolean final_result = false;

    public boolean radians = true;
    public boolean vibration = true;

    public String[] trigonometric_functions = {"sin", "cos", "tan", "asin", "acos", "atan"};

    private Calendar c = Calendar.getInstance();
    private int seconds;
    private int minutes;
    private int hour;
    private int day;
    private int month;
    private String date = "";

    Vibrator vibe = null;

    double final_outcome;

    public String[] themes = {"gradient_purple", "gradient_red", "gradient"};
    public int index = 0;

    LinearLayout background;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int orientation = this.getResources().getConfiguration().orientation;
        setContentView(R.layout.activity_main);


        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Scientific Calculator");
        setSupportActionBar(toolbar);

        display = (EditText) findViewById(R.id.display);

        second = (Button) findViewById(R.id.second);
        sin = (Button) findViewById(R.id.sin);
        cos = (Button) findViewById(R.id.cos);
        tan = (Button) findViewById(R.id.tan);

        divide = (Button) findViewById(R.id.divide);
        multiply = (Button) findViewById(R.id.multiply);
        plus = (Button) findViewById(R.id.plus);
        minus = (Button) findViewById(R.id.minus);

        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        background = (LinearLayout) findViewById(R.id.mainView);

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            change_text(second, "mode<sup><small></small></sup");
            change_text(divide, "&divide;<sub><small><small></small></small></sub>");
            change_text(multiply, "&times;<sub><small><small></small></small></sub>");
            change_text(plus, "+<sub><small><small></small></small></sub>");
        }

        display.setSelection(display.getText().length());

        if (savedInstanceState != null)
        {
            ArrayList<String> radiansList = savedInstanceState.getStringArrayList("Radians");
            ArrayList<String> vibrationList = savedInstanceState.getStringArrayList("Vibration");
            ArrayList<String> expressionList = savedInstanceState.getStringArrayList("Expression");
            ArrayList<String> themeIndex = savedInstanceState.getStringArrayList("Theme");

            System.out.println("Radian list: " + Arrays.toString(radiansList.toArray()));

            radians = Boolean.parseBoolean(radiansList.get(0));
            vibration = Boolean.parseBoolean(radiansList.get(0));
            expression = expressionList.get(0);
            display.setText(expression);
            index = Integer.parseInt(themeIndex.get(0));
            setDrawable(index);
        }
    }

    public void setDrawable(int next_index) {
        if (next_index == 0) {
            background.setBackgroundResource(R.drawable.gradient);
        }
        if (next_index == 1) {
            background.setBackgroundResource(R.drawable.gradient_red);
        }
        if (next_index == 2) {
            background.setBackgroundResource(R.drawable.gradient_purple);
        }
    }

    public void change_theme(View v) {
        index += 1;

        if (index > 2) {
            index = 0;
        }

        setDrawable(index);

    }

    public void uno(View view) {
        final Dialog unoAlert = new Dialog(MainActivity.this);
        unoAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
        unoAlert.setContentView(R.layout.uno_layout);
        unoAlert.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        unoAlert.show();

        Button cancel = (Button) unoAlert.findViewById(R.id.customLayoutCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                unoAlert.dismiss();

            }
        });

        Switch radian_switch = (Switch) unoAlert.findViewById(R.id.radians);
        radian_switch.setChecked(radians);
        radian_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    radians = true;
                    System.out.println("Using radians.");
                }
                else {
                    radians = false;
                    System.out.println("Using degrees.");
                }
            }
        });


    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        ArrayList<String> radianList = new ArrayList<String>();
        radianList.add(String.valueOf(radians));

        ArrayList<String> vibrationList = new ArrayList<String>();
        vibrationList.add(String.valueOf(vibration));

        ArrayList<String> expressionList = new ArrayList<String>();
        expressionList.add(expression);

        ArrayList<String> themeIndex = new ArrayList<String>();
        themeIndex.add(String.valueOf(index));

        outState.putStringArrayList("Radians", radianList);
        outState.putStringArrayList("Vibration", vibrationList);
        outState.putStringArrayList("Expression", expressionList);
        outState.putStringArrayList("Theme", themeIndex);
    }

    public void change_text(Button b, String text) {
        if (b != null) {
            if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                b.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)); // for 24 api and more
            } else {
                b.setText(Html.fromHtml(text)); // or for older api
            }
        }
    }

    public void equals(View v) {
        final_result = true;

        String original_expression = expression;

        System.out.println("Expression: " + String.valueOf(expression));
        expression = expression.replaceAll("\\u221a", "sqrt");
        expression = expression.replaceAll("\\u03a0", "(PI)");
        expression = expression.replaceAll("e", "(E)");
        expression = expression.replaceAll("log", "log10");
        expression = expression.replaceAll("ln", "log");
        expression = expression.replaceAll("dg", "toRadians");

        try {
            double result = Math.round(evaluator.evaluate(expression) * 1000000000d) / 1000000000d;

            String string_output = String.valueOf(result);
            display.setText(string_output);

            date = "/ " + Calendar.getInstance().getTime().toString().substring(0, Calendar.getInstance().getTime().toString().length() - 9) + " /";

            final_outcome = result;
        }
        catch (Exception ex) {
            display.setText("NaN");
            System.out.println(ex.toString());
        }
        finally {

        }
    }

    public static String convert(int n, int base) {
        return Integer.toString(n, base);
    }

    public void onClick(View v) {
        Button button = (Button) v;
        String what_to_append = button.getText().toString();

        String[] add_parenthesis = {"sin", "cos", "tan", "log"};
        String[] operations = {"&divide;", "&times;", "+", "-"};

        if (list_item_in_string(what_to_append, add_parenthesis)) {
            if (second_variant) {
                what_to_append = "a" + what_to_append.substring(0, 3) + "(";

                if (!radians) {
                    what_to_append += "dg(";
                }
            }
            else {
                what_to_append = what_to_append.substring(0, 3) + "(";

                if (!radians) {
                    what_to_append += "dg(";
                }
            }
        }
        else if (what_to_append.equals("ln")) {
            what_to_append = "ln(";
            expression += what_to_append;
        }
        else if (what_to_append.contains("hex")) {
            if (second_variant && final_result) {
                what_to_append = "";
                double result = Double.parseDouble(display.getText().toString());
                String outcome = convert((int) result, 16);

                display.setText(outcome);
                display.setSelection(display.getText().length());

                expression = String.valueOf(expression);
            }
            else {
                what_to_append = "\u00F7";
                expression += "/";
            }
        }
        else if (what_to_append.contains("bin")) {
            System.out.println("Final result: " + final_result + ", Second variant: " + second_variant);
            if (second_variant && final_result) {
                what_to_append = "";
                double result = Double.parseDouble(display.getText().toString());
                String outcome = convert((int) result, 2);

                System.out.println("Outcome: " + outcome);

                display.setText(outcome);
                display.setSelection(display.getText().length());

                expression = String.valueOf(result);
            }
            else {
                what_to_append = "\u00d7";
                expression += "*";
            }
        }
        else if (what_to_append.contains("oct") && second_variant) {
            if (final_result) {
                what_to_append = "";
                double result = Double.parseDouble(display.getText().toString());
                String outcome = convert((int) result, 8);

                display.setText(outcome);
                display.setSelection(display.getText().length());

                expression = String.valueOf(result);
            }
            else {
                what_to_append = "+";
            }
        }
        else if (what_to_append.equals("x\u00b2")) {
            what_to_append = "^2";
            expression += what_to_append;
        }
        else if (what_to_append.contains("%")) {
            what_to_append = "%";
            expression += "* 0.01";
        }
        else if (what_to_append.contains("+") || what_to_append.contains("-")) {
            what_to_append = what_to_append.substring(0, 1);
            expression += what_to_append;
        }
        else if (what_to_append.equals("1/x")) {
            what_to_append = "1/";
            expression += what_to_append;
        }
        else if (what_to_append.contains("\u221a")) {
            what_to_append = "\u221a(";
        }
        else {
            expression += button.getText().toString();
        }

        String display_text = display.getText().toString();

        if (display_text.equals("0") || display_text.equals("0.0") || display_text.equals("NaN")) {
            display.setText(what_to_append);
            expression = what_to_append;
        }
        else {
            display.append(what_to_append);
        }

        display.setSelection(display.getText().length());
        final_result = false;


    }



    public void second(View v) {
        second_variant = !second_variant;

    }

    public boolean list_item_in_string(String string, String[] array) {
        for (String value : array) {
            if (string.contains(value)) {
                return true;
            }
        }

        return false;
    }

    public void toggle_positive_negative(View v) {
        if (final_result) {
            String result = display.getText().toString();
            final_outcome *= -1;
            expression += "*(-1)";
            display.setText(String.valueOf(final_outcome));
        }


    }

    public void clear(View v) {
        expression = "";
        display.setText("0");
        final_result = false;

    }

    public void backspace(View v) {
        String display_text = display.getText().toString();
        String new_text = null;

        if (display_text.equals("0") || display_text.length() <= 1) {
            new_text = "0";
            expression = "";
        }
        else {
            new_text = display_text.substring(0, display_text.length() - 1);
            expression = new_text;
        }

        display.setText(new_text);

        final_result = false;

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {


            case R.id.about:
                startActivity(new Intent(this,About.class));
                break;




        }
        return super.onOptionsItemSelected(item);
    }
}





