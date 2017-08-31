/*
 *  Copyright (C) 2017 wt. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package cn.knightxie.polytopolyimageview;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileOutputStream;

import cn.knightxie.polytopolyimageview.widget.VisualImageView;

import static cn.knightxie.polytopolyimageview.widget.VisualImageView.LR_EXT;
import static cn.knightxie.polytopolyimageview.widget.VisualImageView.LR_INS;
import static cn.knightxie.polytopolyimageview.widget.VisualImageView.TB_EXT;
import static cn.knightxie.polytopolyimageview.widget.VisualImageView.TB_INS;

public class MainActivity
        extends AppCompatActivity
        implements SeekBar.OnSeekBarChangeListener, RadioGroup.OnCheckedChangeListener
{

    private VisualImageView mImage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImage = (VisualImageView) findViewById(R.id.iv_v);
        SeekBar seekBar = (SeekBar) findViewById(R.id.sb);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(100);
        RadioGroup radioGroup = (RadioGroup) findViewById(R.id.rg);
        radioGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
    {
        if (mImage != null)
            mImage.setDelta(progress / 100.0F - 0.5F);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar)
    {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId)
    {
        if (mImage == null) return;
        switch (checkedId)
        {
            case R.id.rb_lr_ins:
                mImage.setMode(LR_INS);
                break;
            case R.id.rb_tb_ins:
                mImage.setMode(TB_INS);
                break;
            case R.id.rb_lr_ext:
                mImage.setMode(LR_EXT);
                break;
            case R.id.rb_tb_ext:
                mImage.setMode(TB_EXT);
                break;
        }
    }

    @Override
    protected void onStop()
    {
        if (mImage != null)
        {
            final Bitmap bitmap = mImage.getOutputBitmap(720);
            new Thread(new Runnable()
            {
                @Override
                public void run()
                {
                    save(bitmap);
                }
            }).start();
        }
        super.onStop();
    }

    private void save(Bitmap bitmap)
    {
        FileOutputStream fos = null;
        try
        {
            File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (dir != null && !dir.exists())
            {
                dir.mkdirs();
            }
            File file = new File(dir, "IMG_" + System.currentTimeMillis() + ".jpg");
            fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fos != null)
                {
                    fos.flush();
                    fos.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
