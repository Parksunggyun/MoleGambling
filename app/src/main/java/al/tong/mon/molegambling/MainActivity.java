package al.tong.mon.molegambling;

import al.tong.mon.molegambling.databinding.ActivityMainBinding;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Toast;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

public class MainActivity extends RxAppCompatActivity {

    List<String> colors;

    private static final int BLANK = -2;
    private static final int INTERRUPT = -1;
    private static final int MAX = 12;
    private static final int FOUR = 4;

    private static final int MAX_GAME_CNT = 20;
    private int CURRENT_GAME_CNT = 0;

    private int correctCount, failedCount;


    int rstGmLevel;

    private int CURR_SEQ;

    ActivityMainBinding binding;

    GridLayoutManager mGridManager;
    CardAdapter mCardAdapter;

    LinearLayoutManager mQuestLayoutManager;
    QuestAdapter mQuestAdapter;

    Vector<Item> items;
    Vector<QuestItem> questItems;

    private int COLOR_CNT, STIMULATION_CNT, INTERRUPTION_CNT, TIME_CNT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        rstGmLevel = 1;
        recyclerView();
    }

    RecyclerView.OnItemTouchListener itemTouchListener = new RecyclerView.OnItemTouchListener() {
        @Override
        public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent evt) {
            switch (evt.getAction()) {
                case MotionEvent.ACTION_UP:
                    View child = recyclerView.findChildViewUnder(evt.getX(), evt.getY());
                    assert child != null;
                    int pos = recyclerView.getChildAdapterPosition(child);
                    if (pos != -1) {
                        Item item = items.get(pos);
                        int seq = item.getSequence();
                        switch (seq) {
                            case -1:
                                Toast.makeText(MainActivity.this, "틀랬습니다.", Toast.LENGTH_SHORT).show();
                                failedCount++;
                                CURRENT_GAME_CNT++;
                                showToast();
                                break;
                            case -2:
                                Toast.makeText(MainActivity.this, "아무것도 아닙니다.", Toast.LENGTH_SHORT).show();
                                //genStimulationItems();
                                break;
                            default:
                                if (seq == CURR_SEQ) {
                                    CURR_SEQ++;
                                    Log.e("seq", String.valueOf(seq));
                                    Log.e("CURR_SEQ", String.valueOf(CURR_SEQ));
                                    Log.e("STIMULATION_CNT", String.valueOf(STIMULATION_CNT));
                                    if ((CURR_SEQ - 1) == STIMULATION_CNT && seq == STIMULATION_CNT) {
                                        Toast.makeText(MainActivity.this, "정답입니다.", Toast.LENGTH_SHORT).show();
                                        correctCount++;
                                        CURRENT_GAME_CNT++;
                                        showToast();
                                    }
                                } else {
                                    Toast.makeText(MainActivity.this, "틀랬습니다.", Toast.LENGTH_SHORT).show();
                                    failedCount++;
                                    CURRENT_GAME_CNT++;
                                    showToast();
                                }
                                break;
                        }
                    }
                    break;
            }
            return false;
        }

        @Override
        public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean b) {
        }
    };

    private void showToast() {
        if (CURRENT_GAME_CNT == MAX_GAME_CNT) {
            if(correctCount >= 12) {
                if(rstGmLevel <= 9) {
                    Toast.makeText(this, "12문제 이상 맞추셨으므로 다음 단계로 올라갑니다", Toast.LENGTH_SHORT).show();
                    rstGmLevel++;
                } else {
                    Toast.makeText(this, "12문제 이상 맞추셨지만 최고 단계이므로 단계가 유지됩니다", Toast.LENGTH_SHORT).show();
                }
            } else {
                if(rstGmLevel == 1) {
                    Toast.makeText(this, "12문제 미만 맞추셨으므로 단계가 유지됩니다", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "12문제 미만 맞추셨으므로 이전 단계로 내려갑니다", Toast.LENGTH_SHORT).show();
                    rstGmLevel--;
                }
            }
            binding.colorBtnListView.removeOnItemTouchListener(itemTouchListener);
            recyclerView();
        } else {
            genStimulationItems();
        }
    }



    private void questRecyclerView() {
        mQuestLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mQuestAdapter = new QuestAdapter(this);
        binding.questCircleListView.setLayoutManager(mQuestLayoutManager);
        binding.questCircleListView.setAdapter(mQuestAdapter);
    }


    private void recyclerView() {
        initGame(rstGmLevel);
        questRecyclerView();
        mGridManager = new GridLayoutManager(this, FOUR);
        mCardAdapter = new CardAdapter(this);
        binding.colorBtnListView.setLayoutManager(mGridManager);
        binding.colorBtnListView.setAdapter(mCardAdapter);
        binding.colorBtnListView.addOnItemTouchListener(itemTouchListener);
        binding.colorBtnListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int width = binding.colorBtnListView.getLayoutParams().width / FOUR;
                int height = binding.colorBtnListView.getLayoutParams().height / 3;
                mCardAdapter.setSize(width, height);
                mCardAdapter.notifyDataSetChanged();
                genStimulationItems();
                binding.colorBtnListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    private void genStimulationItems() {
        items = new Vector<>();
        questItems = new Vector<>();
        Vector<String> gen = genColors(STIMULATION_CNT);
        int unPressed = 0;
        int pressed = 0;
        int questColor = 0;
        for (int i = 0; i < gen.size(); i++) {
            String currColor = gen.get(i);
            switch (currColor) {
                case "#EB5465":
                    unPressed = R.drawable.block_01_on_re;
                    pressed = R.drawable.block_01_off_re;
                    questColor = R.drawable.border_mole_quest_red;
                    break;
                case "#F96E51":
                    unPressed = R.drawable.block_02_on_or;
                    pressed = R.drawable.block_02_off_or;
                    questColor = R.drawable.border_mole_quest_orange;
                    break;
                case "#FFCC59":
                    unPressed = R.drawable.block_03_on_ye;
                    pressed = R.drawable.block_03_off_ye;
                    questColor = R.drawable.border_mole_quest_yellow;
                    break;
                case "#9FD16E":
                    unPressed = R.drawable.block_04_on_gr;
                    pressed = R.drawable.block_04_off_gr;
                    questColor = R.drawable.border_mole_quest_green;
                    break;
                case "#5ECFFF":
                    unPressed = R.drawable.block_05_on_sky;
                    pressed = R.drawable.block_05_off_sky;
                    questColor = R.drawable.border_mole_quest_sky;
                    break;
                case "#3E6DDB":
                    unPressed = R.drawable.block_06_on_bl;
                    pressed = R.drawable.block_06_off_bl;
                    questColor = R.drawable.border_mole_quest_blue;
                    break;
                case "#AC92ED":
                    unPressed = R.drawable.block_07_on_pu;
                    pressed = R.drawable.block_07_off_pu;
                    questColor = R.drawable.border_mole_quest_purple;
                    break;
            }
            items.add(new Item(i + 1, currColor, unPressed, pressed));
            questItems.add(new QuestItem(i + 1, questColor));
        }
        if (INTERRUPTION_CNT != 0) {
            for (int i = 0; i < INTERRUPTION_CNT; i++) {
                String currColor = gen.get(i);
                switch (currColor) {
                    case "#EB5465":
                        unPressed = R.drawable.block_01_xon_re;
                        pressed = R.drawable.block_01_xoff_re;
                        break;
                    case "#F96E51":
                        unPressed = R.drawable.block_02_xon_or;
                        pressed = R.drawable.block_02_xoff_or;
                        break;
                    case "#FFCC59":
                        unPressed = R.drawable.block_03_xon_ye;
                        pressed = R.drawable.block_03_xoff_ye;
                        break;
                    case "#9FD16E":
                        unPressed = R.drawable.block_04_xon_gr;
                        pressed = R.drawable.block_04_xoff_gr;
                        break;
                    case "#5ECFFF":
                        unPressed = R.drawable.block_05_xon_sky;
                        pressed = R.drawable.block_05_xoff_sky;
                        break;
                    case "#3E6DDB":
                        unPressed = R.drawable.block_06_xon_bl;
                        pressed = R.drawable.block_06_xoff_bl;
                        break;
                    case "#AC92ED":
                        unPressed = R.drawable.block_07_xon_pu;
                        pressed = R.drawable.block_07_xoff_pu;
                        break;
                }
                items.add(new Item(INTERRUPT, currColor, unPressed, pressed));
            }
        }
        int currSize = items.size();
        for (int i = currSize; i < MAX; i++) {
            items.add(new Item(BLANK, "#D8D8D8", R.drawable.block_00_on_ba, R.drawable.block_00_off_ba));
        }
        CURR_SEQ = 1;
        Collections.shuffle(items);
        mCardAdapter.update(items);
        mQuestAdapter.update(questItems);
    }

    private Vector<String> genColors(int colorCnt) {
        Collections.shuffle(colors);
        Vector<String> gen = new Vector<>();
        for (int i = 0; i < colorCnt; i++) {
            gen.add(colors.get(i));
        }
        return gen;
    }

    /**
     * @param rstGmLevel from 1 to 10
     */
    private void initGame(int rstGmLevel) {
        int currLevelIcon = 0;
        switch (rstGmLevel) {
            case 1:
                setCnt(2, 2, 0, 10);
                currLevelIcon = R.drawable.icon_game_level1;
                break;
            case 2:
                setCnt(3, 3, 0, 10);
                currLevelIcon = R.drawable.icon_game_level2;
                break;
            case 3:
                setCnt(4, 4, 0, 10);
                currLevelIcon = R.drawable.icon_game_level3;
                break;
            case 4:
                setCnt(4, 4, 1, 15);
                currLevelIcon = R.drawable.icon_game_level4;
                break;
            case 5:
                setCnt(5, 5, 0, 15);
                currLevelIcon = R.drawable.icon_game_level5;
                break;
            case 6:
                setCnt(5, 5, 1, 15);
                currLevelIcon = R.drawable.icon_game_level6;
                break;
            case 7:
                setCnt(6, 6, 1, 15);
                currLevelIcon = R.drawable.icon_game_level7;
                break;
            case 8:
                setCnt(6, 6, 2, 20);
                currLevelIcon = R.drawable.icon_game_level8;
                break;
            case 9:
                setCnt(6, 7, 1, 20);
                currLevelIcon = R.drawable.icon_game_level9;
                break;
            case 10:
                setCnt(7, 7, 2, 20);
                currLevelIcon = R.drawable.icon_game_level9;
                break;

        }
        colors = Arrays.asList("#EB5465", "#F96E51", "#FFCC59", "#9FD16E", "#5ECFFF", "#3E6DDB", "#AC92ED");
        Collections.shuffle(colors);
        CURRENT_GAME_CNT = 0;
        correctCount = 0;
        failedCount = 0;
        binding.currLevelImgView.setImageResource(currLevelIcon);
    }


    /**
     * @param counts idx 0 = color count,
     *               idx 1 = stimulation count,
     *               idx 2 = interruption count,
     *               idx 3 = time out
     */
    private void setCnt(int... counts) {
        COLOR_CNT = counts[0];
        STIMULATION_CNT = counts[1];
        INTERRUPTION_CNT = counts[2];
        TIME_CNT = counts[3];
    }

}