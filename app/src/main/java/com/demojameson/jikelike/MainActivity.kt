package com.demojameson.jikelike

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var count: Int = 0
    private var count2: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        count = jikeTextView.getText().toIntOrNull() ?: 0
        count2 = jikeTextView2.getText().toIntOrNull() ?: 0

        linearLayout.setOnClickListener {
            jikeLikeView.setChecked(!jikeLikeView.getChecked())
            jikeLikeView2.setChecked(!jikeLikeView2.getChecked())

            if (jikeLikeView.getChecked()) {
                count++
                jikeTextView.setText(count.toString())
            } else {
                count--
                jikeTextView.setText(count.toString(), JikeTextView.AnimationType.DOWN)
            }

            count2++
            jikeTextView2.setText(count2.toString())
        }

    }
}
