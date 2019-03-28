package al.tong.mon.molegambling;

import al.tong.mon.molegambling.databinding.ActivityMainBinding;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    private static final int FOUR = 4;

    ActivityMainBinding binding;

    GridLayoutManager mGridManager;
    CardAdapter mCardAdapter;

    Vector<Item> items;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        items = new Vector<>();

        mGridManager = new GridLayoutManager(this, FOUR);
        mCardAdapter = new CardAdapter(this);

        binding.colorBtnListView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int length = binding.colorBtnListView.getWidth() / FOUR;
                mCardAdapter.setSize(length);
                mCardAdapter.notifyDataSetChanged();

                binding.colorBtnListView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
}