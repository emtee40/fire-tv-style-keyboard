package com.example.semitop7.FireTVStyle;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.TextView;

public class FireTVStyleIME extends InputMethodService {

    private InputMethodManager mInputMethodManager;
    private InputConnection ic;
    private FrameLayout linear;
    private View kv;
    private View keyboardView;
    private TextView preview;

    private String keyRu[] = LangSymbols.KEY_RU_ABV;
    private String keyEn[] = LangSymbols.KEY_EN_ABC;
    //private String keyRu[] = LangSymbols.KEY_RU_YCU;
    //private String keyEn[] = LangSymbols.KEY_EN_QWE;

    private String allSymb[] = LangSymbols.SYMBOLS;

    final private int firstFocus = R.id.cr00;

    @Override
    public void onCreate() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            enableHardwareAcceleration();
        }
        super.onCreate();
        mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public View onCreateInputView() {
        //kv = (keyboard == true) ? setLang(keyRu) : setLang(keyEn);
        keyboardView = getLayoutInflater().inflate(R.layout.keyboard, null);
        preview = (TextView) keyboardView.findViewById(R.id.preview);
        //linear = (FrameLayout) keyboardView.findViewById(R.id.keyboard1);
        //linear.addView(kv);
        //keyboardView.invalidate();
        return null;
    }

    @Override
    public View onCreateCandidatesView() {
        kv = (keyboard == true) ? setLang(keyRu) : setLang(keyEn);
        linear = (FrameLayout) keyboardView.findViewById(R.id.keyboard1);
        linear.addView(kv);
        keyboardView.invalidate();
        return keyboardView;
    }

    @Override
    public void onStartInput(EditorInfo editorinfo, boolean flag) {
        super.onStartInput(editorinfo, flag);
        setImeOptions(getResources(), editorinfo);
        //if(kv!=null) {
        //btnBack.setPressed(false);
        //kv.findViewById(firstFocus).requestFocusFromTouch();
        //if(kv.isInTouchMode()) {
        //kv.findViewById(firstFocus).requestFocusFromTouch();
        //} else kv.findViewById(firstFocus).requestFocus();
        //}
        //setBackDisposition(BACK_DISPOSITION_WILL_DISMISS);
    }

    @Override
    public void onFinishInputView(boolean flag) {
        m = 0;
        s = 0;
        handlerStart.removeCallbacks(runnableStrat);
        super.onFinishInputView(flag);
        if (kv != null) {
            if (focusButton != null) {
                focusButton.setPressed(false);
            }
            btnBack.setPressed(false);
            btnSpace.setPressed(false);
            btnDelete.setPressed(false);
            btnNext.setPressed(false);
            btnLang.setPressed(false);
        }

        //ic=null;
    }

    private int cursorEnd;
    private ExtractedText extractedText;
    private boolean close;

    @Override
    public void onStartInputView(EditorInfo editorinfo, boolean flag) {
        super.onStartInputView(editorinfo, flag);
        setImeOptions(getResources(), editorinfo);
        updateInputViewShown();
        if (close)
            handleClose();
        else {
            ic = new IcWrapper(getCurrentInputConnection(), false);
            cursorEnd = 0;
            cursorStart = 0;
            textLength = 0;
            if (ic != null) {
                //btnNext.setText(String.valueOf(ic));
                extractedText = ic.getExtractedText(new ExtractedTextRequest(), 0);
                if (extractedText != null) {
                    if (extractedText.selectionEnd == 0 && extractedText.text.length() < 101) {
                        cursorEnd = extractedText.text.length();
                        ic.setSelection(cursorEnd, cursorEnd);
                    }
                }
            }
            if (symb)
                btnLang.performClick();
            try {
                handlerStart = new Handler();
                runnableStrat = new Runnable() {
                    @Override
                    public void run() {
                        if (isInputViewShown() && !kv.hasFocus() && kv != null && !close) {
                            kv.findViewById(firstFocus).requestFocusFromTouch();
                            handlerStart.postDelayed(runnableStrat, 300);
                        }
                    }
                };
                handlerStart.postDelayed(runnableStrat, 200);
            } catch (Exception e) {
            }
        }
        //btnNext.setText(String.valueOf(actionEnter));

    }

    private boolean style;
    private IconButton btnClear;
    private IconButton btnLang;
    private IconButton btnSpace;
    private IconButton btnBack;
    private IconButton btnDelete;
    private IconButton btnNext;
    private IconButton btnSymbols;
    private IconButton btnShift;
    private IconButton btnCursorLeft;
    private IconButton btnCursorRight;
    private boolean shiftKey;
    private boolean capsKey;
    private boolean keyboard = true;
    private boolean symb;
    private int m;
    private int s;
    private Handler handlerStart;
    private Runnable runnableStrat;
    private Handler handlerCaps;
    private Runnable runnableCaps;

    public void mySetAllCaps(int textDpSize) {
        s++;
        if (s == 1) {
            handlerCaps = new Handler();
            runnableCaps = new Runnable() {
                @Override
                public void run() {
                    s = 0;
                }
            };
            shiftKey = !shiftKey;
            if (capsKey) {
                capsKey = false;
            }

            spannableTextAndColorShift(btnShift, LangSymbols.А_А, textDpSizeFull);
            for (int i = 0; i < 40; i++) {
                final IconButton childButton = (IconButton) parentView.getChildAt(i);
                spannableTextAndColor(childButton, childButton.getText().toString(), textDpSize, colorsSup);
            }
            btnLang.setAllCaps(shiftKey);
            handlerCaps.postDelayed(runnableCaps, 300);
        }
        if (s == 2) {
            s = 0;
            handlerCaps.removeCallbacks(runnableCaps);
            if (shiftKey) {
                capsKey = true;
                spannableTextAndColorShift(btnShift, LangSymbols.А_А, textDpSizeFull);
            }
        } else
            spannableTextAndColorShift(btnShift, LangSymbols.А_А, textDpSizeFull);
    }

    public void changeLang(String[] lang, int textDpSize) {
        if (symb) {
            btnBack.setNextFocusUpId(R.id.cr32);
            btnLang.setNextFocusRightId(R.id.space);
            btnSpace.setNextFocusLeftId(R.id.lang);
            kv.findViewById(R.id.cr32).setNextFocusDownId(R.id.back);
            kv.findViewById(R.id.cr33).setNextFocusDownId(R.id.back);
        } else {
            btnBack.setNextFocusUpId(R.id.shift);
            btnLang.setNextFocusRightId(R.id.shift);
            btnSpace.setNextFocusLeftId(R.id.symbols);
            kv.findViewById(R.id.cr32).setNextFocusDownId(R.id.shift);
            kv.findViewById(R.id.cr33).setNextFocusDownId(R.id.symbols);
        }
        for (int i = 0; i < 40; i++) {
            final IconButton childButton = (IconButton) parentView.getChildAt(i);
            String childButtonText = lang[i];
            spannableTextAndColor(childButton, childButtonText, textDpSize, colorsSup);
            //childButton.setText(lang[i]);
        }

    }

    private ViewGroup parentView;
    private ColorStateList colors;
    private ColorStateList colorsSup;
    private int textDpSize;
    private int textDpSizeFull;
    private int cursorStart;
    private int textLength;

    public View setLang(String[] symbols) {
        final int keySize = (int) (getResources().getDimension(R.dimen.key_size) + 0.5f);
        final int marginSize = (int) (getResources().getDimension(R.dimen.keyboard_margin) + 0.5f);
        textDpSize = (int) (getResources().getDimension(R.dimen.input_text_size) + 0.5f);
        textDpSizeFull = (int) (getResources().getDimension(R.dimen.input_text_size_full) + 0.5f);
        kv = getLayoutInflater().inflate(R.layout.grid_all, null);

        //style = true;
        if (style) {
            kv.setBackgroundResource(R.color.color_btn);
        }

        btnClear = (IconButton) kv.findViewById(R.id.clear);
        btnLang = (IconButton) kv.findViewById(R.id.lang);
        btnSpace = (IconButton) kv.findViewById(R.id.space);
        btnBack = (IconButton) kv.findViewById(R.id.back);
        btnDelete = (IconButton) kv.findViewById(R.id.delete);
        btnNext = (IconButton) kv.findViewById(R.id.next);
        btnSymbols = (IconButton) kv.findViewById(R.id.symbols);
        btnShift = (IconButton) kv.findViewById(R.id.shift);
        btnCursorLeft = (IconButton) kv.findViewById(R.id.cursorLeft);
        btnCursorRight = (IconButton) kv.findViewById(R.id.cursorRight);

        parentView = (ViewGroup) kv;
        for (int i = 0; i < parentView.getChildCount(); i++) {
            final IconButton childButton = (IconButton) parentView.getChildAt(i);
            final int id = childButton.getId();
            if (style) {
                childButton.setBackgroundResource(R.drawable.btn_shape_standart);
            }

            childButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textDpSize);
            try {
                XmlResourceParser parser = getResources().getXml(R.xml.btn_text_color);
                XmlResourceParser parserSup = getResources().getXml(R.xml.btn_sup_text_color);
                colors = ColorStateList.createFromXml(getResources(), parser);
                colorsSup = ColorStateList.createFromXml(getResources(), parserSup);
                childButton.setTextColor(colors);
                spannableTextAndColor(childButton, symbols[i], textDpSize, colorsSup);
            } catch (Exception e) {
            }

            //childButton.setTextColor(getResources().getColor(R.btn_text_color.button_text_color));
            childButton.setAllCaps(false);
            GridLayout.LayoutParams params = (GridLayout.LayoutParams) childButton.getLayoutParams();
            params.setMargins(marginSize, marginSize, marginSize, marginSize);
            params.width = keySize;
            params.height = keySize;
            childButton.setLayoutParams(params);

            childButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (capsKey) {
                        btnShift.performClick();
                        return true;
                    }
                    return false;
                }
            });

            childButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (firstButtonText != null && m == 1 && secondPress && !firstButtonText.toLowerCase().equals(childButton.getText().toString().toLowerCase())) {
                        m = 0;
                        if (shiftKey || capsKey) {
                            ic.commitText(firstButtonText.substring(0, 1).toUpperCase(), 1);
                            if (!capsKey)
                                mySetAllCaps(textDpSize);
                        } else {
                            ic.commitText(firstButtonText.substring(0, 1).toLowerCase(), 1);
                        }
                    }
                    extractedText = ic.getExtractedText(new ExtractedTextRequest(), 0);
                    if (extractedText != null) {
                        cursorStart = extractedText.selectionEnd;
                        textLength = extractedText.text.length();
                    }
                    switch (id) {
                        case R.id.cursorLeft:
                            if (cursorStart > 0)
                                ic.setSelection(cursorStart - 1, cursorStart - 1);
                            break;
                        case R.id.cursorRight:
                            if (cursorStart < textLength)
                                ic.setSelection(cursorStart + 1, cursorStart + 1);
                            break;
                        case R.id.lang:
                            if (symb) {
                                symb = false;
                                keyboard = !keyboard;
                                disableButton(btnSymbols, btnShift);
                            }
                            if (keyboard) {
                                keyboard = false;
                                changeLang(keyEn, textDpSize);
                                btnLang.setText(LangSymbols.ABV);
                            } else {
                                keyboard = true;
                                changeLang(keyRu, textDpSize);
                                btnLang.setText(LangSymbols.ABC);
                            }
                            break;
                        case R.id.space:
                            ic.commitText(" ", 1);
                            break;
                        case R.id.delete:
                            if (ic.getSelectedText(0) == null)
                                ic.deleteSurroundingText(deleteSpeed, 0);
                            else
                                ic.commitText("", 1);
                            break;
                        case R.id.clear:
                            try {
                                extractedText = ic.getExtractedText(new ExtractedTextRequest(), 0);
                                ic.deleteSurroundingText(extractedText.selectionStart - 0, extractedText.text.length() - extractedText.selectionEnd);
                                //ic.performContextMenuAction(android.R.id.selectAll);
                                //ic.commitText("", 1);
                            } catch (Exception e) {
                            }
                            break;
                        case R.id.back:
                            handleClose();
                            break;
                        case R.id.next:
                            if (inputType == InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE || inputType == InputType.TYPE_TEXT_FLAG_MULTI_LINE)
                                sendDownUpKeyEvents(KeyEvent.KEYCODE_ENTER);
                            else
                                ic.performEditorAction(actionEnter);
                            //ic.performEditorAction(0);
                            break;
                        case R.id.symbols:
                            symb = true;
                            changeLang(allSymb, textDpSize);
                            if (keyboard)
                                btnLang.setText(LangSymbols.ABV);
                            else
                                btnLang.setText(LangSymbols.ABC);
                            disableButton(btnSymbols, btnShift);
                            btnLang.requestFocusFromTouch();
                            break;
                        case R.id.shift:
                            mySetAllCaps(textDpSize);
                            //spannableTextAndColorShift(btnShift, "аА", textDpSizeFull);
                            break;
                        default:
                            final String childBtnText = childButton.getText().toString();
                            if (ic != null) {
                                if (doubleClickButton(childButton, childBtnText, textDpSize)) {
                                } else if (shiftKey || capsKey) {
                                    ic.commitText(childBtnText.substring(0, 1).toUpperCase(), 1);
                                    //shiftKey=!shiftKey;
                                    if (!capsKey)
                                        mySetAllCaps(textDpSize);
                                    //spannableTextAndColorShift(btnShift, "аА", textDpSizeFull);
                                } else {
                                    //ic.commitText(childBtnText.toLowerCase(), 1);
                                    ic.commitText(childBtnText.substring(0, 1).toLowerCase(), 1);
                                }
                            }
                            break;
                    }
                }
            });
        }

        largeButtonTextSize(textDpSizeFull, btnNext, btnBack, btnSpace, btnLang, btnDelete, btnClear, btnSymbols);
        largeButtonTextBacground(btnNext, btnBack);
        spannableTextAndColorShift(btnShift, LangSymbols.А_А, textDpSizeFull);
        return kv;
    }

    public void disableButton(IconButton... btn) {
        float alpfa;
        if (symb)
            alpfa = 0.25f;
        else
            alpfa = 1f;
        for (IconButton b : btn) {
            b.setFocusable(!symb);
            b.setEnabled(!symb);
            b.setAlpha(alpfa);
            b.invalidate();
        }
    }

    public void spannableTextAndColorShift(final IconButton childButton, String btnText, int textDpSizeFull) {
        SpannableString shiftText;
        if (capsKey)
            shiftText = new SpannableString(btnText.toUpperCase());
        else
            shiftText = new SpannableString(btnText);

        int focusedCapsOn = getResources().getColor(R.color.color_background_ftv);
        int colorCapsOn = getResources().getColor(R.color.button_text_color);
        int colorCapsOff = getResources().getColor(R.color.color_transparent_shift);
        int focusedCapsOff = getResources().getColor(R.color.color_transparent_shift_focused);


        if (shiftKey) {
            int three = colorCapsOn;
            colorCapsOn = colorCapsOff;
            colorCapsOff = three;
            three = focusedCapsOn;
            focusedCapsOn = focusedCapsOff;
            focusedCapsOff = three;
        }

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_pressed},
                new int[]{android.R.attr.state_focused},
                new int[]{android.R.attr.state_hovered},
                new int[]{},
        };
        int[] colors1 = new int[]{
                focusedCapsOn,
                focusedCapsOn,
                focusedCapsOn,
                colorCapsOn
        };
        int[] colors2 = new int[]{
                focusedCapsOff,
                focusedCapsOff,
                focusedCapsOff,
                colorCapsOff
        };

        ColorStateList myListColor1 = new ColorStateList(states, colors1);
        ColorStateList myListColor2 = new ColorStateList(states, colors2);
        if (capsKey) {
            shiftText.setSpan(new TextAppearanceSpan(null, 0, 0, myListColor2, null), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            shiftText.setSpan(new TextAppearanceSpan(null, 0, 0, myListColor1, null), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            shiftText.setSpan(new TextAppearanceSpan(null, 0, 0, myListColor2, null), 1, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        childButton.setText(shiftText);
        childButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, textDpSizeFull);
    }

    public void spannableTextAndColor(final IconButton childButton, String btnText, int textDpSize, ColorStateList colorStateList) {
        SpannableString doubleSymbols;
        if (shiftKey)
            btnText = btnText.toUpperCase();
        else
            btnText = btnText.toLowerCase();
        switch (btnText.toLowerCase()) {
            case "ї і":
            case "і ї":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 3);
                childButton.setText(doubleSymbols);
                break;
            case "щш":
            case "шщ":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
                childButton.setText(doubleSymbols);
                break;
            case "ъь":
            case "ьъ":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
                childButton.setText(doubleSymbols);
                break;
            case "ёе":
            case "её":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
                childButton.setText(doubleSymbols);
                break;
            case "ґг":
            case "гґ":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
                childButton.setText(doubleSymbols);
                break;
            case LangSymbols.ABC:
            case LangSymbols.ABV:
            case LangSymbols.BUTTON_SYMB:
                childButton.setText(btnText);
                break;
            case LangSymbols.SPACE:
                childButton.setText(getResources().getString(R.string.space));
                break;
            case LangSymbols.DELETE:
                childButton.setText(getResources().getString(R.string.delete));
                break;
            case LangSymbols.CLEAR:
                childButton.setText(getResources().getString(R.string.clear));
                break;
            case LangSymbols.BACK:
                childButton.setText(getResources().getString(R.string.previous));
                break;
            case LangSymbols.NEXT:
                childButton.setText(getResources().getString(R.string.next));
                break;
            //case "аа":
                //break;
            //case "/@":
            //case "@/":
            //doubleSymbols = new SpannableString(btnText);
            //setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
            //childButton.setText(doubleSymbols);
            //break;
            case ",.":
            case ".,":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 2);
                childButton.setText(doubleSymbols);
                break;
            case "; :":
            case ": ;":
                doubleSymbols = new SpannableString(btnText);
                setSpannableString(doubleSymbols, textDpSize, colorStateList, 3);
                childButton.setText(doubleSymbols);
                break;
            default:
                childButton.setText(btnText.substring(0, 1));
                break;
        }
        //button.setTextColor(myListColor);
    }


    public void setSpannableString(SpannableString btnText, int textDpSize, ColorStateList colorStateList, int end) {
        btnText.setSpan(new TextAppearanceSpan(null, 0, textDpSize, null, null), 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        btnText.setSpan(new SuperscriptSpan(), 1, end, 0);
        btnText.setSpan(new RelativeSizeSpan(0.4f), 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        btnText.setSpan(new TextAppearanceSpan(null, 0, 0, colorStateList, null), 1, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String firstButtonText;
    private boolean secondPress;
    private boolean trueKeybaord;
    private boolean trueSymb;
    private Handler handler;
    private Runnable runnable;
    private boolean doubleDouble;

    public boolean doubleClickButton(final IconButton childButton, final String btnText, final int textDpSize) {

        String secondText = null;
        doubleDouble = false;
        switch (btnText.toLowerCase()) {
            case "і ї":
                //case "ї і":
                secondText = "ї і";
                break;
            case "шщ":
                //case "щш":
                secondText = "щш";
                break;
            case "ьъ":
                //case "ъь":
                secondText = "ъь";
                break;
            case "её":
                //case "ёе":
                secondText = "ёе";
                break;
            case "гґ":
                //case "ґг":
                secondText = "ґг";
                break;
            //case "/@":
            //case "@/":
            //secondText="@/";
            //break;
            case ": ;":
                //case "; :":
                secondText = "; :";
                break;
            case ".,":
                //case ",.":
                secondText = ",.";
                break;
            case "ї і":
            case "щш":
            case "ъь":
            case "ёе":
            case "ґг":
                //case "@/":
            case "; :":
            case ",.":
                doubleDouble = true;
                break;
        }
        if (secondText != null || doubleDouble) {
            if (doubleDouble) {
                m += 2;
                secondText = btnText;
            } else
                m++;
            if (m == 1) {
                secondPress = true;
                firstButtonText = btnText;
                trueKeybaord = keyboard;
                trueSymb = symb;
                handler = new Handler();
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (secondPress && m == 1) {
                            if (shiftKey) {
                                ic.commitText((firstButtonText.substring(0, 1).toUpperCase()), 1);
                                if (!capsKey)
                                    mySetAllCaps(textDpSize);
                            } else {
                                ic.commitText(firstButtonText.substring(0, 1), 1);
                            }
                        }
                        m = 0;
                    }
                };
                handler.postDelayed(runnable, 300);
            } else if (m == 2) {
                secondPress = false;
                handler.removeCallbacks(runnable);
                m = 0;
                if (!doubleDouble) {
                    //doubleSecondText = btnText;
                    final Handler doubleHandler = new Handler();
                    Runnable doubleRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (trueSymb == symb && trueKeybaord == keyboard) {
                                spannableTextAndColor(childButton, btnText, textDpSize, colorsSup);
                                //doubleDouble=false;
                            }
                        }
                    };
                    doubleHandler.postDelayed(doubleRunnable, 1000);
                    spannableTextAndColor(childButton, secondText, textDpSize, colorsSup);
                }
                if (shiftKey) {
                    ic.commitText((secondText.substring(0, 1).toUpperCase()), 1);
                    if (!capsKey)
                        mySetAllCaps(textDpSize);
                } else
                    ic.commitText(secondText.substring(0, 1), 1);
                //}
            }
            return true;
        }
        if (btnText.length() > 1)
            return true;
        else
            return false;
    }

    public void largeButtonTextSize(int textDpSizeFull, IconButton... btnTextSize) {
        for (IconButton b : btnTextSize) {
            b.setTextSize(TypedValue.COMPLEX_UNIT_PX, textDpSizeFull);
        }
    }

    public void largeButtonTextBacground(IconButton... btnBacground) {
        for (IconButton b : btnBacground) {
            b.setBackgroundResource(R.drawable.btn_shape_long_ftv);
        }
    }

    private IconButton focusButton;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (firstButtonText != null && m == 1 && keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode != KeyEvent.KEYCODE_NUMPAD_ENTER && keyCode != KeyEvent.KEYCODE_ENTER) {
                m = 0;
                if (shiftKey || capsKey) {
                    ic.commitText(firstButtonText.substring(0, 1).toUpperCase(), 1);
                    //shiftKey=!shiftKey;
                    if (!capsKey)
                        mySetAllCaps(textDpSize);
                } else {
                    ic.commitText(firstButtonText.substring(0, 1).toLowerCase(), 1);
                }
            }

            if (isInputViewShown() && kv.hasFocus() && kv != null) {
                switch (keyCode) {
                    case (KeyEvent.KEYCODE_DPAD_LEFT):
                        focusMove(keyboardView.FOCUS_LEFT, event);
                        return true;

                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        focusMove(keyboardView.FOCUS_RIGHT, event);
                        return true;

                    case KeyEvent.KEYCODE_DPAD_UP:
                        focusMove(keyboardView.FOCUS_UP, event);
                        return true;

                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        focusMove(keyboardView.FOCUS_DOWN, event);
                        return true;

                    case KeyEvent.KEYCODE_MEDIA_REWIND:
                    case KeyEvent.KEYCODE_VOLUME_DOWN:
                        onKeyLongButton(btnDelete, event);
                        return true;

                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_NUMPAD_ENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        focusButton = (IconButton) getWindow().getCurrentFocus();
                        int focusBtn = focusButton.getId();
                        if (focusBtn == btnDelete.getId())
                            onKeyLongButton(btnDelete, event);
                        if (focusBtn == btnCursorLeft.getId())
                            onKeyLongButton(btnCursorLeft, event);
                        if (focusBtn == btnCursorRight.getId())
                            onKeyLongButton(btnCursorRight, event);
                        else
                            onKeyDownUp(focusButton, event, keyCode);
                        return true;
                }

                if (event.getRepeatCount() == 0) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_MENU:
                        case KeyEvent.KEYCODE_MEDIA_STOP:
                            onKeyDownUp(btnLang, event, keyCode);
                            return true;

                        case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        case KeyEvent.KEYCODE_MEDIA_PLAY:
                            onKeyDownUp(btnNext, event, keyCode);
                            return true;

                        case KeyEvent.KEYCODE_VOLUME_UP:
                        case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                            onKeyDownUp(btnSpace, event, keyCode);
                            return true;

                        case KeyEvent.KEYCODE_BACK:
                            onKeyDownUp(btnBack, event, keyCode);
                            return true;

                    }
                }
            } else if (isInputViewShown() && kv != null) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    btnBack.requestFocus();
                    btnBack.setPressed(true);
                    btnBack.invalidate();
                }
                return true;
            }
            if (blockKey && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))
                return true;
        } catch (Exception e) {
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER)
            return onKeyDown(KeyEvent.KEYCODE_DPAD_CENTER, event);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (isInputViewShown() && kv.hasFocus() && kv != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_ENTER:
                    focusButton = (IconButton) getWindow().getCurrentFocus();
                    onKeyDownUp(focusButton, event, keyCode);
                    return true;

                case KeyEvent.KEYCODE_MENU:
                case KeyEvent.KEYCODE_MEDIA_STOP:
                    onKeyDownUp(btnLang, event, keyCode);
                    return true;

                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    onKeyDownUp(btnNext, event, keyCode);
                    return true;

                case KeyEvent.KEYCODE_VOLUME_UP:
                case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                    onKeyDownUp(btnSpace, event, keyCode);
                    return true;

                case KeyEvent.KEYCODE_MEDIA_REWIND:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    onKeyDownUp(btnDelete, event, keyCode);
                    return true;

                case KeyEvent.KEYCODE_BACK:
                    onKeyDownUp(btnBack, event, keyCode);
                    return true;

            }
        } else if (isInputViewShown() && kv != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                btnBack.performClick();
            } else {
                kv.findViewById(firstFocus).requestFocusFromTouch();
            }
            return true;
        }

        if (blockKey && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER)) {
            blockKey = false;
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_ENTER)
            return onKeyUp(KeyEvent.KEYCODE_DPAD_CENTER, event);
        return super.onKeyUp(keyCode, event);
    }

    private boolean blockKey;

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (isInputViewShown() && kv.hasFocus() && kv != null) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_CENTER:
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_ENTER:
                    focusButton = (IconButton) getWindow().getCurrentFocus();
                    if (focusButton.getId() == btnNext.getId()) {
                        handleClose();
                        sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
                        blockKey = true;
                    } else
                        btnShift.performLongClick();

                    break;
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                    handleClose();
                    sendDownUpKeyEvents(KeyEvent.KEYCODE_DPAD_DOWN);
                    break;
            }
            return true;
        }
        if (blockKey && (keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY || keyCode == KeyEvent.KEYCODE_NUMPAD_ENTER))
            return true;
        if (keyCode == KeyEvent.KEYCODE_ENTER)
            return onKeyLongPress(KeyEvent.KEYCODE_DPAD_CENTER, event);
        return super.onKeyLongPress(keyCode, event);
    }

    public void focusMove(int focus, KeyEvent event) {
        if (event.getRepeatCount() % 2 == 0) {
            getWindow().getCurrentFocus().focusSearch(focus).requestFocus();
        }
    }

    private int deleteSpeed = 1;

    public void onKeyLongButton(IconButton btn, KeyEvent event) {
        if (event.getRepeatCount() == 0) {
            btn.setPressed(true);
            btn.invalidate();
        }

        if (event.getRepeatCount() > 0) {

            if (btn.getId() == btnCursorLeft.getId() || btn.getId() == btnCursorRight.getId()) {
                btn.performClick();
            } else if (event.getRepeatCount() % 2 == 0) {
                btn.performClick();
            }

            if (event.getRepeatCount() % 17 == 0) {
                if (btn.getId() == btnDelete.getId())
                    deleteSpeed++;
            }
        }
    }

    public void onKeyDownUp(IconButton btn, KeyEvent event, int keyCode) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            event.startTracking();
            if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE) {
                btn.requestFocus();
            }
            btn.setPressed(true);
            btn.invalidate();
        }
        if (btn != null && event.getAction() == KeyEvent.ACTION_UP) {
            if (deleteSpeed != 1)
                deleteSpeed = 1;
            btn.performClick();
            btn.setPressed(false);
            btn.invalidate();
        }
    }

    private int actionEnter;
    private int inputType;

    public void setImeOptions(Resources res, EditorInfo options) {
        /*if (btnNext == null) {
            return;
        }
        switch (options.imeOptions&(EditorInfo.IME_MASK_ACTION|EditorInfo.IME_FLAG_NO_ENTER_ACTION)) {
		//switch (options.imeOptions) {
			case EditorInfo.IME_ACTION_NEXT:
                //btnNext.setText(R.string.Next);
                //btnNext.invalidate();
                actionEnter=EditorInfo.IME_ACTION_NEXT;
                break;
            case EditorInfo.IME_ACTION_GO:
                //btnNext.setText(R.string.Go);
                //btnNext.invalidate();
                actionEnter=EditorInfo.IME_ACTION_GO;
                break;
            case EditorInfo.IME_ACTION_SEARCH:
                //btnNext.setText(R.string.Search);
                //btnNext.invalidate();
                actionEnter=EditorInfo.IME_ACTION_SEARCH;
                break;
            case EditorInfo.IME_ACTION_SEND:
                //btnNext.setText(R.string.Send);
               // btnNext.invalidate();
                actionEnter=EditorInfo.IME_ACTION_SEND;
                break;
		    case EditorInfo.IME_ACTION_DONE:
				actionEnter=EditorInfo.IME_ACTION_DONE;
				break;
		    case EditorInfo.IME_ACTION_PREVIOUS:
				actionEnter=EditorInfo.IME_ACTION_PREVIOUS;
				break;
		    case 0:
                close=true;
				break;
            default:
                actionEnter=EditorInfo.IME_ACTION_NONE;;
                break;
        }*/
        inputType = options.inputType & (InputType.TYPE_TEXT_FLAG_IME_MULTI_LINE | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        actionEnter = options.imeOptions & EditorInfo.IME_MASK_ACTION;
        if (actionEnter == 0) {
            close = true;
        } else
            close = false;
    }

    @Override
    public boolean onEvaluateFullscreenMode() {
        return false;
    }

    @Override
    public void onComputeInsets(InputMethodService.Insets outInsets) {
        super.onComputeInsets(outInsets);
        if (!isFullscreenMode()) {
            outInsets.touchableInsets = outInsets.visibleTopInsets;
        }
    }

    @Override
    public void onWindowShown() {
        setCandidatesViewShown(true);
    }

    @Override
    public boolean onEvaluateInputViewShown() {
        return !close;
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
    }

    private void handleClose() {
        requestHideSelf(0);
    }


    private class IcWrapper extends InputConnectionWrapper {

        IcWrapper(InputConnection target, boolean mutable) {
            super(target, mutable);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            ExtractedText extractedText = getExtractedText(new ExtractedTextRequest(), 0);
            int start = extractedText.selectionStart;
            int end = extractedText.selectionEnd;
            CharSequence p1 = preview.getText().subSequence(0, start);
            CharSequence p3 = preview.getText().subSequence(end, preview.length());
            preview.setText(p1 + "" + text + "" + p3);

            return super.commitText(text, newCursorPosition);
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            ExtractedText extractedText = getExtractedText(new ExtractedTextRequest(), 0);
            int start = extractedText.selectionStart;
            int end = extractedText.selectionEnd;
            if (start - beforeLength < 0)
                return false;
            CharSequence p1 = preview.getText().subSequence(0, start - beforeLength);
            CharSequence p2 = preview.getText().subSequence(start, end);
            CharSequence p3 = preview.getText().subSequence(end + afterLength, preview.length());
            preview.setText(p1 + "" + p2 + "" + p3);

            boolean res = super.deleteSurroundingText(beforeLength, afterLength);
            return res;
        }

        @Override
        public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
            ExtractedText extractedText = super.getExtractedText(request, flags);
            preview.setText(extractedText.text);
            return extractedText;
        }
    }
}
