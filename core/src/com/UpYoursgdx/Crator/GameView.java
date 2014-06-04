package com.UpYoursgdx.Crator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class GameView implements ApplicationListener, InputProcessor {
    SpriteBatch batch;  //All drawing is done using this to speed up rendering performance.
                        //Screen is only drawn once batch.end is called, which should be after
                        //  all game code for the cycle has been completed.
    GameingView GV;     //Object that holds all the game code.
    public Preferences prefs;
    float ScreenWidth,ScreenHeight;

    @Override
    public void create() {
        batch = new SpriteBatch();
        //prefs is a way to store small bits of data when the app is closed.
        //It is passed to GV (GameView) where is is used.
        prefs = Gdx.app.getPreferences("my-preferences");
        GV = new GameingView();
        ScreenWidth = Gdx.graphics.getWidth();
        ScreenHeight = Gdx.graphics.getHeight();
        GV.Setup(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(),prefs);
        Gdx.input.setInputProcessor(this); //Allow for touches to be directed to the current app

        //Allow for 5 fingers to touch the screen at a time
        for(int i = 0; i < 5; i++){
            touches.put(i, new TouchInfo());
        }

        Gdx.input.setCatchBackKey(true); //Allow back key to be handled as a keyEvent below
        Gdx.graphics.setContinuousRendering(true); //Make The game render at 60fps (Lower depending on how
                                                   //much the CPU has to do per cycle!!!)
    }

    @Override
    public void resize(int width, int height) {
        //Game Will Always be Landscape. Not necessary.
    }

    @Override
    public void render() {
        //Draw Black screen before every frame
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //This is run at 60fps
        //So keep it efficient \/\/\/\/
        batch.begin();      //Open Drawing canvas
        GV.onDraw(batch);   //Game logic -> Draw on canvas.
        batch.end();        //Close drawing canvas. Display on screen.
    }

    @Override
    public void pause() {
        GV.Pause(); //Handled by GV
    }

    @Override
    public void resume() {
        GV.Resume(); //Handled by GV
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    class TouchInfo {
        //################################################
        //# Store Touch data for a single finger (Allow for Multi-touch)
        //################################################
        public float touchX = 0;
        public float touchY = 0;
        public boolean touched = false;
    }

    //################################################
    //# Create an array of TouchInfo Objects > touches. Each touches corresponds to a finger.
    //################################################
    private Map<Integer,TouchInfo> touches = new HashMap<Integer,TouchInfo>();

    @Override
    public boolean keyDown(int keycode) {
        //Handle BACK button \/ (Esc on PC)
        if(keycode == Input.Keys.BACK || keycode == Input.Keys.ESCAPE){
            switch (GV.iGamemode) {
                case 1:     //1-GMSPLASH
                    //Game will crash if exited from splash.
                    break;
                case 2:     //2-GMUSERSPACE
                    //Assume user wants to quit
                    Gdx.app.exit();
                    break;
                case 3:     //3-GMPAUSE
                    //Assume user wants to go to Upgrade Screen.
                    GV.iGamemode = 5;
                    GV.UpdateGame();
                    break;
                case 4:     //4-GMPLAY
                    //Assume user wants to go to Upgrade Screen.
                    GV.iGamemode = 5;
                    GV.UpdateGame();
                    break;
                case 5:     //5-GMUPGRADE
                    //User wants to see stats
                    GV.iGamemode = 2;
                    GV.UpdateGame();
                    break;
                case 6:     //6-Debug
                    break;
            }
        }

        //Handle computer keys \/\/\/
        if (keycode == Input.Keys.SPACE) {
            float TouchX = 0;
            float TouchY = 0;
            int pointer = 1;
            if(pointer < 5){
                touches.get(pointer).touchX = TouchX;
                touches.get(pointer).touchY = TouchY;
                GV.TouchHandler(TouchX,TouchY);
                touches.get(pointer).touched = true;
            }
            return true;
        } else if (keycode == Input.Keys.CONTROL_RIGHT) {
            float TouchX = ScreenWidth;
            float TouchY = 0;
            int pointer = 2;
            if(pointer < 5){
                touches.get(pointer).touchX = TouchX;
                touches.get(pointer).touchY = TouchY;
                GV.TouchHandler(TouchX,TouchY);
                touches.get(pointer).touched = true;
            }
            return true;
        } else return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        //Computer compatibility \/\/
        if (keycode == Input.Keys.SPACE) {
            float TouchX = 0;
            float TouchY = 0;
            int pointer = 1;
            if(pointer < 5){
                GV.ReleaseHandler(TouchX, TouchY);
                touches.get(pointer).touchX = 0;
                touches.get(pointer).touchY = 0;
                touches.get(pointer).touched = false;
            }
            return true;
        } else if (keycode == Input.Keys.CONTROL_RIGHT) {
            float TouchX = ScreenWidth;
            float TouchY = 0;
            int pointer = 2;
            if(pointer < 5){
                GV.ReleaseHandler(TouchX, TouchY);
                touches.get(pointer).touchX = 0;
                touches.get(pointer).touchY = 0;
                touches.get(pointer).touched = false;
            }
            return true;
        } else return false;

    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        float TouchX;
        float TouchY;
        TouchX = screenX;
        //The top for the touch system is the bottom for the
        //  drawing system. Therefore touch must be inversed to
        //  register beneath users finger.
        TouchY = ScreenHeight - screenY;
        if(pointer < 5){ //Only 5 in the map
            touches.get(pointer).touchX = TouchX;
            touches.get(pointer).touchY = TouchY;
            GV.TouchHandler(TouchX,TouchY);
            touches.get(pointer).touched = true;
        }
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        float TouchX;
        float TouchY;
        TouchX = screenX;
        TouchY = ScreenHeight - screenY;
        if(pointer < 5){
            GV.ReleaseHandler(TouchX, TouchY);
            //^^Give touch event to GameView before de-registering it \/\/
            touches.get(pointer).touchX = 0;
            touches.get(pointer).touchY = 0;
            touches.get(pointer).touched = false;
        }
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float TouchX;
        float TouchY;
        TouchX = screenX;
        TouchY = ScreenHeight - screenY;
        //Used to drag sliders.
        GV.DragHandler(TouchX,TouchY);
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    //Draw Texture to a Rect
    public void DrawToRect(Rect r, Texture tx, SpriteBatch sBtch) {
        sBtch.draw(tx, r.left, r.bottom, r.right - r.left, r.top - r.bottom);
        GV.IncRDrawPerSec();
    }

    @Override
    public void dispose() {
        GV.Dispose();
    }


    class Rect {
        //Basic Rectangle
        //Simplifies a lot of code.
        float left, top, right, bottom;

        //Get center of Rect (horizontal)
        public float CenterX() {
            return (left + right)/2;
        }
        //Get center of Rect (Vertical)
        public float CenterY() {
            return (top + bottom)/2;
        }
        //Get width of Rect
        public float width() {
            return right - left;
        }
        //Get height of Rect
        public float height() {
            return top - bottom;
        }
        //Move Rect to upper left, all points off screen.
        public void OffScreen() {
            left = - 10;
            right = - 10;
            top = - 10;
            bottom = -10;
        }
        //Turn into square as large as possible within rectangle(
        // ##############_________################
        // # Rectangle  | New     |              #
        // #            | Square  |              #
        // #            |         |              #
        // #            |         |              #
        // ##############---------################
        public void CopySquare(Rect r,float rPadding) {
            if (r.height() >= r.width()) {
                left = r.CenterX() - ((r.width()/2) - rPadding);
                right = r.CenterX() + ((r.width()/2) - rPadding);
                top = r.CenterY() + ((r.width()/2) - rPadding);
                bottom = r.CenterY() - ((r.width()/2) - rPadding);
            } else {
                left = r.CenterX() - ((r.height()/2) - rPadding);
                right = r.CenterX() + ((r.height()/2) - rPadding);
                top = r.CenterY() + ((r.height()/2) - rPadding);
                bottom = r.CenterY() - ((r.height()/2) - rPadding);
            }
        }
        //Define rectangle in one line.
        public void equals(float leftP, float topP, float rightP, float bottomP) {
            left = leftP;
            right = rightP;
            top = topP;
            bottom = bottomP;
        }
        public void MoveLeft(float fAmount) {
            left = left - fAmount;
            right = right - fAmount;
        }
        public void MoveDown(float fAmount) {
            top = top - fAmount;
            bottom = bottom - fAmount;
        }
        //Copy numerical values of given rect
        // (Saying Rect1 = Rect2 seems to make Rect1 point to Rect2 instead of
        //  just copying it)
        public void RectCopy(Rect r) {
            left = r.left;
            right = r.right;
            top = r.top;
            bottom = r.bottom;
        }
    }

    class GameingView {

        ////////////////////////////////////////////////////
        //////////////////Variables/////////////////////////
        ////////////////////////////////////////////////////

        public final Integer //?Integer Values for Weapon types (Non changeable)
                iR = 1,
                iP = 2,
                iS = 3,
                iBe = 4,
                iBa = 5;
        private Integer
                ScreenX,
                ScreenY,
                iMaxUpgradeLevel = 10,
                iSplashCount,
                iPlayCount,
                iTouchScrler,
                iNuked,
                iDownSinLevel;
        public Integer
                iBulletCosts[] = {3,5,15,20,15},
                iGamemode,
                iFPS,
                iFPS2,
                iDrawsPerSec,
                iFinalDrawSec;
                //1-GMSPLASH,2-GMUSER,3-GMPAUSE,4-GMPLAY,5-GMUPGRADE;
        final int TargetFPS = 60;
        private long
                LastloopTime = System.nanoTime(),
                OptimalTime = 1000000000 / TargetFPS,       //1 Nanosecond = 1 / 1 000 000 000 Seconds.
                LastDrawCountTime,
                LastFPSCountTime;
        private Float
                iUnit,
                fChange,
                fHardChange,
                fAcceleration,
                fSpeed = 0f,
                PlayerX,
                PlayerY;
                                            //  GAME CLASSES {
        private ScoreKeeper SK;
        private DistanceCounter DC;
        private UpgradeSpace Upgs;
        private BoxRegulator brBoxes;
        private UserSpace MainScreen;
        private PauseMenu pauseMenu;
        private SceneManager SM;
        private SoundPlayer SP;
        private TouchLayout TL;
        private SettingDropDown SDD;
        private Weapon AK47;
        private player pYou;               //  }
        private BitmapFont
                fnt,
                nfnt;
        private BitmapFont.TextBounds
                tb;
        private Rect
                rFullScreen = new Rect(),
                rBKG = new Rect(),
                rBar = new Rect(),
                rSplash = new Rect(),
                rTopBar = new Rect();
        private Texture
                imgBlack,
                imgWhite,
                imgSplash,
                imgCoginner,
                imgCogBkg,
                imgSun,
                imgBlueBkg,
                imgCoin;
        private Boolean
                bPaused,
                bVibrate = true,
                bSound = true,
                bFlipped = false,
                bDoneLoading = false;
        private Preferences prefs;
        private Random
                rand = new Random();
        private Runnable runa;
        private final int
                isndCLick = 1,
                isndHiHat  = 2,
                isndKnock = 3,
                isndKnockClick = 4,
                isndLiftDis = 5,
                isndLiftUp = 6,
                isndLowBAss = 7,
                isndWindSelected = 8,
                isndBeam = 9,
                isndColtShot = 10,
                isndImpact = 11,
                isndLoadShotGun = 12,
                isndLongSwitch = 13,
                isndRotarySwitch = 14,
                isndShortSwitch = 15;

        /////////////////////////////////////////////////////
        /////////////////Code:   ////////////////////////////
        /////////////////////////////////////////////////////

        public void IncRDrawPerSec() {
            /*iDrawsPerSec++;
            Long now = System.nanoTime();
            if (now - LastDrawCountTime > 100000000) {
                iFinalDrawSec = iDrawsPerSec* 10;
                iDrawsPerSec = 0;
                LastDrawCountTime = System.nanoTime();
            }*/
        }

        public void ReleaseHandler(float iX, float iY) {
            if (bDoneLoading)
                if (SDD.bSettingsUp) {
                    switch (iGamemode) {
                        case 1:     //1-GMSPLASH

                            break;
                        case 2:     //2-GMUSERSPACE
                            MainScreen.ReleaseDetector(iX, iY);
                            if (bVibrate) Gdx.input.vibrate(10);
                            break;
                        case 3:     //3-GMPAUSE
                            pauseMenu.ReleaseDetector(iX, iY);
                            break;
                        case 4:     //4-GMPLAY
                            break;
                        case 5:     //5-GMUPGRADE
                            Upgs.releaseDetector(iX, iY);
                            break;
                        case 6:     //6-Debug
                            break;
                    }
                }
        }
        public void DragHandler (float iX, float iY) {
            if (bDoneLoading)
                if (SDD.bSettingsUp) {
                    if (iTouchScrler == -1)
                        switch (iGamemode) {
                            case 1:     //1-GMSPLASH
                                break;
                            case 2:     //2-GMUSERSPACE
                                break;
                            case 3:     //3-GMPAUSE
                                break;
                            case 4:     //4-GMPLAY
                                break;
                            case 5:     //5-GMUPGRADE
                                Upgs.MoveDetector(iX, iY);
                                break;
                            case 6:     //6-Debug
                                break;
                        }
                    else {
                        switch (iTouchScrler) {
                            case 1:
                                Upgs.scrl.MoveTouch(iX, iY);
                                break;
                            case 2:
                                Upgs.scrl2.MoveTouch(iX, iY);
                                break;
                            case 3:
                                Upgs.scrl3.MoveTouch(iX, iY);
                                break;
                        }
                    }
                }
        }

        public void Pause() {
            switch (iGamemode) {
                case 1:     //1-GMSPLASH

                    break;
                case 2:     //2-GMUSERSPACE
                    break;
                case 3:     //3-GMPAUSE
                    break;
                case 4:     //4-GMPLAY
                    SDD.SettingsDown();
                    SDD.iAngle = 0;
                    break;
                case 5:     //5-GMUPGRADE
                    break;
                case 6:     //6-Debug
                    break;
            }
        }
        public void Resume() {
            pYou.Resume();
            brBoxes.Resume();
            SM.Resume();
        }
        public void TouchHandler(float iX, float iY) {
            if (bDoneLoading)
                if (SDD.bSettingsUp) {
                    switch (iGamemode) {
                        case 1:     //1-GMSPLASH

                            break;
                        case 2:     //2-GMUSERSPACE
                            MainScreen.touchDetector(iX, iY);
                            break;
                        case 3:     //3-GMPAUSE
                            pauseMenu.touchDetector(iX, iY);
                            SDD.TouchHandler(iX, iY);
                            break;
                        case 4:     //4-GMPLAY
                            if (bFlipped) {
                                if (iX < ScreenX / 2) {
                                    fSpeed = -((float) ScreenY / 110);
                                } else {
                                    Rect pR = new Rect();
                                    pR.left = pYou.PlayerRect.left;
                                    pR.right = pYou.PlayerRect.right;
                                    pR.top = pYou.PlayerRect.top;
                                    pR.bottom = pYou.PlayerRect.bottom;
                                    AK47.Shoot(pR.left + ((pR.right - pR.left) / 2), pR.top + ((pR.top - pR.bottom) / 2));
                                    if (bVibrate) Gdx.input.vibrate(15);
                                    pYou.ShowSign("-" + iBulletCosts[AK47.iBulType-1], iX, iY, 135);
                                }
                            } else {
                                if (iX > ScreenX / 2) {
                                    fSpeed = -((float) ScreenY / 110);
                                } else {
                                    Rect pR = new Rect();
                                    pR.left = pYou.PlayerRect.left;
                                    pR.right = pYou.PlayerRect.right;
                                    pR.top = pYou.PlayerRect.top;
                                    pR.bottom = pYou.PlayerRect.bottom;
                                    AK47.Shoot(pR.left + ((pR.right - pR.left) / 2), pR.top + ((pR.top - pR.bottom) / 2));
                                    if (bVibrate) Gdx.input.vibrate(15);
                                    pYou.ShowSign("Minus", iX, iY, 135);
                                }
                            }
                            SDD.TouchHandler(iX, iY);
                            break;
                        case 5:     //5-GMUPGRADE
                            Upgs.touchDetector(iX, iY);

                            SDD.TouchHandler(iX, iY);
                            break;
                        case 6:     //6-Debug
                            if (bVibrate) Gdx.input.vibrate(10);
                            break;
                    }
                } else if (bDoneLoading) {
                    SDD.TouchHandler(iX, iY);
                }
        }

        protected void onDraw(SpriteBatch batch1) {
            Long now = System.nanoTime();
            Long TimeTaken = now - LastloopTime;
            LastloopTime = now;
            double delta = TimeTaken / (double) OptimalTime;
            GameUpdate(delta, batch1);
            /*iFPS++;
            if (now - LastFPSCountTime > 1000000000) {
                iFPS2 = iFPS;
                iFPS = 0;
                LastFPSCountTime = System.nanoTime();
            }
            fnt.setScale(0.025f*iUnit);
            fnt.setColor(1,1,1,1);
            fnt.draw(batch1, iFPS2 + " Fps   " + iFinalDrawSec + " Dps", 0, ScreenY);
            fnt.setScale(0.05f*iUnit);*/
        }

        public void Setup(Integer iWidth, Integer iHeight,Preferences prefp) {
            iDrawsPerSec = 0;
            iDownSinLevel = 0;
            LastFPSCountTime = 0;
            iFPS2 = 0;
            iFPS = 0;
            prefs = prefp;
            iNuked = 0;
            ScreenX = iWidth;
            ScreenY = iHeight;
            runa = new Runnable() {
                @Override
                public void run() {
                    SplashSetup();
                }
            };
            iUnit = ScreenY / 100f;
            nfnt = new BitmapFont(Gdx.files.internal("number.fnt"),false);
            nfnt.setScale(0.05f * iUnit);
            nfnt.setColor(1, 1, 1, 1);
            fnt = new BitmapFont(Gdx.files.internal("droidsans.fnt"),false);
            fnt.setScale(0.05f*iUnit);
            fnt.setColor(1, 1, 1, 1);

            rTopBar.top = ScreenY + (float)(Math.sin(Math.toRadians(iDownSinLevel)) * rTopBar.height());
            rTopBar.left = 0;
            rTopBar.right = ScreenX;
            rTopBar.bottom = ScreenY - (15 * iUnit) + (float)(Math.sin(Math.toRadians(iDownSinLevel)) * rTopBar.height());

            rFullScreen.left = 0;
            rFullScreen.top = ScreenY;
            rFullScreen.bottom = 0;
            rFullScreen.right = ScreenX;
            fChange = 0.2f * iUnit;
            fHardChange = 0.2f * iUnit;
            fAcceleration = 0.06f * iUnit;
            rBar.left = 0;
            rBar.right = ScreenX;
            rBar.top =ScreenY - (15 * iUnit);
            rBar.bottom = 0;
            SK = new ScoreKeeper();
            SK.Setup();
            bVibrate = SK.GetBoolean("bVibrate");
            bSound = SK.GetBoolean("bSound");
            bFlipped = SK.GetBoolean("bFlipped");
            DC = new DistanceCounter();
            DC.Setup();
            pYou = new player();
            pYou.Setup();
            iTouchScrler = -1;
            iGamemode = 1;
            if (bSound) {
            }
            SP = new SoundPlayer();
            SP.Setup();
            UpdateGame();

        }
        public void SplashSetup() {
            tb = new BitmapFont.TextBounds();
            MainScreen = new UserSpace();
            MainScreen.Setup();


            pauseMenu = new PauseMenu();
            pauseMenu.Setup();
            SM = new SceneManager();
            SM.Setup();
            brBoxes = new BoxRegulator();
            brBoxes.Setup();
            SK = new ScoreKeeper();
            SK.Setup();
            Upgs = new UpgradeSpace();
            Upgs.Setup();
            SK.SetHighScore(SK.GetValue("highscore"));
            TL = new TouchLayout();
            TL.Setup();
            AK47 = new Weapon();
            AK47.Setup();
            SDD = new SettingDropDown();
            SDD.Setup();
        }
        //idea each character has own weapon system, more expensive characters have added perks. (Maybe infomatics system)(tells them when to jump for certain boxes)
        public void UpdateGame() {
            SP.EndBassLine();
            switch (iGamemode) {
                case 1:     //1-GMSPLASH UpdateGame
                    imgSplash = new Texture("splashWM2.jpg");
                    imgBlack = new Texture("black.jpg");
                    imgCoginner = new Texture("UI/btncog.png");
                    imgCogBkg = new Texture("UI/btncogback.png");
                    imgWhite = new Texture("white.jpg");
                    imgSun = new Texture("Costumes/GodGlow.png");
                    imgBlueBkg = new Texture("GreyBkg.jpg");
                    imgCoin = new Texture("UI/CashSign.png");
                    rSplash.left = 0;
                    rSplash.top = ScreenY - (ScreenY - (ScreenX / 2)) / 2;
                    rSplash.bottom = (ScreenY - (ScreenX / 2)) / 2;
                    rSplash.right = ScreenX;
                    iSplashCount = 0;
                    bPaused = false;
                    break;
                case 2:     //2-GMUSERSPACE UpdateGame
                    iSplashCount = 0;
                    MainScreen.Setup();
                    SM.Reset(true);
                    bPaused = false;
                    break;
                case 3:     //3-GMPAUSE UpdateGame
                    pauseMenu.Reset();
                    SK.AddCash(SK.iScore);
                    SK.Save();
                    bPaused = true;
                    break;
                case 4:     //4-GMPLAY UpdateGame
                    iSplashCount = 0;
                    SM.Reset(false);
                    fSpeed = 0f;
                    brBoxes.ResetBoxes();
                    pYou.Reset();
                    iPlayCount = 0;
                    SK.ResScore();
                    AK47.Reset(Upgs.ch[SK.iPlayerLevel].iWeapDraw[Upgs.ch[SK.iPlayerLevel].iWeapSelected]);
                    pYou.ShowSign("CATCH!",bFlipped?  ScreenX / 4:(ScreenX / 5) * 3, (50*iUnit), 180);

                    bPaused = false;
                    DC.Reset();
                    TL.Reset();
                    SP.Reset();
                    SP.StartBassLine();
                    iDownSinLevel = 0;
                    break;
                case 5:     //5-GMUPGRADE UpdateGame
                    iSplashCount = 0;
                    bPaused = false;
                    Upgs.Reset();
                    iSplashCount = 0;
                    rBKG.top = ScreenY - (20 * iUnit);
                    rBKG.left = (20 * iUnit);
                    rBKG.right = ScreenX - (20 * iUnit);
                    rBKG.bottom = (20 * iUnit);
                    SM.Reset(true);
                    fSpeed = 0f;
                    brBoxes.ResetBoxes();
                    pYou.Reset();
                    iPlayCount = 0;
                    SK.ResScore();
                    AK47.Reset(Upgs.ch[SK.iPlayerLevel].iWeapDraw[Upgs.ch[SK.iPlayerLevel].iWeapSelected]);
                    DC.Reset();
                    TL.Reset();
                    SP.Reset();
                    break;
                case 6:     //6-Debug
                    iSplashCount = 0;
                    break;
            }
        }

        public void GameUpdate(double delta, SpriteBatch batch1) {
            fChange = fChange * (float) delta;
            SP.Update();
            switch (iGamemode) {
                case 1:     //1-GMSPLASH
                    iSplashCount = iSplashCount + 1;
                    DrawToRect(rSplash, imgSplash, batch1);
                    if (iSplashCount <= 100) {
                        batch1.setColor(1,1,1,1- (iSplashCount*0.01f));
                        DrawToRect(rFullScreen,imgBlack,batch1);
                        batch1.setColor(1,1,1,1);
                    } else if (iSplashCount == 200) {
                        runa.run();
                    } else if ((iSplashCount > 200) && (iSplashCount < 300)) {
                        batch1.setColor(1,1,1,((iSplashCount - 200)*0.01f));
                        DrawToRect(rFullScreen, imgBlack, batch1);
                        batch1.setColor(1, 1, 1, 1);
                    } else if (iSplashCount == 300) {
                        iGamemode = 2;
                        bDoneLoading = true;
                        UpdateGame();
                        DrawToRect(rFullScreen, imgBlack, batch1);
                    }
                    break;
                case 2:     //2-GMUSERSPACE
                    SM.Update(fChange,(float)delta);
                    SM.DrawScene(batch1);

                    batch1.setColor(0, 0, 0, 0.6f);
                    DrawToRect(rBar,imgBlack,batch1);
                    batch1.setColor(1, 1, 1, 1);
                    MainScreen.DrawSpace(batch1);
                    if (iSplashCount < 100) {
                        batch1.setColor(1, 1, 1, 1 - (iSplashCount * 0.01f));
                        DrawToRect(rFullScreen, imgBlack, batch1);
                        batch1.setColor(1, 1, 1, 1);
                    }
                    iSplashCount += 1;
                    break;
                case 3:     //3-GMPAUSE
                    batch1.setColor(1, 1, 1, 1);
                    DrawToRect(rFullScreen, imgBlack, batch1);
                    batch1.setColor(1, 1, 1, 1);
                    SM.DrawScene(batch1);
                    DC.DrawDist(batch1);
                    pYou.DrawScore(batch1);
                    brBoxes.DrawBoxes(batch1, false);
                    pYou.DrawPlayer(batch1);
                    pauseMenu.DrawPause(batch);
                    SDD.Draw(batch1);
                    break;
                case 4:     //4-GMPLAY
                    SM.Update(fChange * 1.5f,(float)delta);
                    SM.DrawScene(batch1);
                    if (iPlayCount > 50 && !bPaused && bSound) {
                        fSpeed += fAcceleration * (float) delta;
                        pYou.MoveDown(fSpeed * (float) delta);
                    } else {
                        iPlayCount++;
                    }
                    brBoxes.MoveLeft(fChange * ((SK.iDiffLevel < 1)?0.36f : 0.45f));
                    brBoxes.DrawBoxes(batch1, true);
                    AK47.MoveBullets(fChange);
                    AK47.Draw(batch1);
                    pYou.DrawPlayer(batch1);
                    if (iSplashCount < 180) {
                        iSplashCount += 2;
                    } else if (iSplashCount == 180) {
                        iSplashCount = 200;
                        pYou.ShowSign("Now SHOOT!", !bFlipped ? ScreenX / 4 : (ScreenX / 5) * 3, (50*iUnit), 180);
                    }
                    SM.bal.DrawInfront(batch1);
                    SDD.Draw(batch1);
                    pYou.DrawScore(batch1);
                    DC.DrawDist(batch1);
                    TL.DrawTouch(batch1);
                    break;
                case 5:     //5-GMUPGRADE
                    iSplashCount++;
                    SM.Update(fChange,(float)delta);
                    SM.DrawScene(batch1);
                    pYou.DrawPlayer(batch1);
                    pYou.DrawScore(batch1);
                    DC.DrawDist(batch1);
                    Upgs.Draw(batch1);
                    SDD.Draw(batch1);
                    if (iSplashCount < 100) {
                        batch1.setColor(1,1,1,1 - (iSplashCount*0.01f));
                        DrawToRect(rFullScreen, imgBlack, batch1);
                        batch1.setColor(1, 1, 1, 1);
                        iSplashCount += 1;
                    }

                    break;
                case 6:     //6-Debug
                    break;
            }
            if (iNuked > 0) iNuked --;
            batch1.setColor(1,1,1,(float)Math.sin(Math.toRadians(iNuked)));
            DrawToRect(rFullScreen, imgWhite, batch1);
            fChange = fHardChange;
            pYou.DrawSign(batch1);
            batch1.setColor(1,1,1,1);

        }

        public void SetHighScore(Integer iS) {
            SK.SetHighScore(iS);
        }

        public Integer GetHighScore() {
            return SK.GetHighScore();
        }

        public Boolean CollisionTest(Rect r1, Rect r2) {
            return (r1.right > r2.left && r1.top > r2.bottom && r1.bottom < r2.top && r1.left < r2.right);
        }
        public void Dispose() {
            MainScreen.Destroy();
            pauseMenu.Destroy();
            pYou.Destroy();
            imgBlack.dispose();
            imgWhite.dispose();
            imgSplash.dispose();
            imgCoginner.dispose();
            imgCogBkg.dispose();
            imgSun.dispose();
            SM.Destroy();
            TL.Destroy();
            SDD.Destroy();
            brBoxes.Destroy();
            MainScreen.Destroy();
            pauseMenu.Destroy();
            AK47.Destroy();
        }

        ///////////////////////////////////////////////
        /////////////Classes || OBjects////////////////
        ///////////////////////////////////////////////

        class TimeKeeper {


        }/////////////////////////
        //////////////////////////////
        class ScoreKeeper {
            public Integer
                    iScore,
                    iHighScore,
                    iCash,
                    iGunLevel,
                    iMaxGunLevel,
                    iPlayerLevel,
                    iMaxPlayerLevel,
                    iDiffLevel,
                    iMaxDiffLevel;
            public Rect
                    rCashSign = new Rect();
            public void DrawCash(Rect r,SpriteBatch batch1,float fTextSize) {
                rCashSign.left = r.left;
                rCashSign.right = r.left + r.height();
                rCashSign.bottom = r.bottom;
                rCashSign.top = r.top;
                DrawToRect(rCashSign,imgCoin,batch1);

                String sDisplay2 = "" + iCash.toString();
                String sDisplay = "";
                Integer iSpace = 0;
                for (int i = sDisplay2.length() - 1; i >= 0 ; i--) {
                    sDisplay = "" + sDisplay2.charAt(i) + sDisplay;
                    iSpace++;
                    if (iSpace == 3) {
                        sDisplay = " " + sDisplay;
                        iSpace = 0;
                    }
                }
                nfnt.setScale(fTextSize);
                nfnt.getBounds(sDisplay,tb);
                nfnt.draw(batch1,sDisplay,rCashSign.right + 2*iUnit,rCashSign.CenterY() + (tb.height/2));
            }

            public void Setup() {
                iScore = 0;
                iHighScore = 0;
                iCash = 0;
                RetrieveCash();
                iGunLevel = GetValue("GunLevel");
                iMaxGunLevel = GetValue("MaxGunLevel");
                iPlayerLevel = GetValue("PlayerLevel");
                iMaxPlayerLevel = GetValue("MaxPlayerLevel");
                iDiffLevel = GetValue("iDiffLevel");
                iMaxDiffLevel = GetValue("iMaxDiffLevel");
            }

            public void Save() {
                SaveValue("GunLevel", iGunLevel);
                SaveValue("PlayerLevel", iPlayerLevel);
                SaveValue("iDiffLevel", iDiffLevel);
                SaveValue("MaxGunLevel", iMaxGunLevel);
                SaveValue("MaxPlayerLevel", iMaxPlayerLevel);
                SaveValue("iMaxDiffLevel", iMaxDiffLevel);
                SaveValue("iCash", iCash);
                SaveValue("highscore", iHighScore);
            }

            public void resHigh() {
                iHighScore = 0;
                SaveValue("highscore", iHighScore);
            }

            public void ResScore() {
                iScore = 0;
            }

            public void SetHighScore(Integer iS) {
                iHighScore = iS;
            }

            public Integer GetHighScore() {
                if (iScore > iHighScore) {
                    iHighScore = iScore;
                }
                SaveValue("highscore", iHighScore);
                return iHighScore;
            }

            public Integer GetScore() {
                return iScore;
            }

            public void IncScore(Integer iMuch) {
                iScore += iMuch;
            }

            public void DecScore(Integer iMuch) {
                iScore -= iMuch;
                if (iScore < 0) iScore = 0;
                pYou.EnlScore();
            }

            public void SaveValue(String sName,Boolean bVal) {
                prefs.putInteger(sName, bVal ? 0 : 1);
                prefs.flush();
            }
            public void SaveValue(String sName, Integer iVal) {
                prefs.putInteger(sName, iVal);
                prefs.flush();
            }
            public Boolean GetBoolean(String sBoolName) {
                return (prefs.getInteger(sBoolName) == 0);
            }

            public Integer GetValue(String sName) {
                return prefs.getInteger(sName);
            }

            public void RetrieveCash() {
                iCash = GetValue("iCash");
                if (iCash == 0) iCash = 500;
            }

            public Integer GetCash() {
                return iCash;
            }

            public String GetFormattedCash(Integer iCash) {
                String sDisplay2 = "" + iCash.toString();
                String sDisplay = "";
                Integer iSpace = 0;
                for (int i = sDisplay2.length() - 1; i >= 0 ; i--) {
                    sDisplay = "" + sDisplay2.charAt(i) + sDisplay;
                    iSpace++;
                    if (iSpace == 3) {
                        sDisplay = " " + sDisplay;
                        iSpace = 0;
                    }
                }
                return "$" + sDisplay;
            }

            public String GetCashf() {
                String sDisplay2 = "" + GetCash();
                String sDisplay = "";
                Integer iSpace = 0;
                for (int i = sDisplay2.length() - 1; i >= 0 ; i--) {
                    sDisplay = "" + sDisplay2.charAt(i) + sDisplay;
                    iSpace++;
                    if (iSpace == 3) {
                        sDisplay = " " + sDisplay;
                        iSpace = 0;
                    }
                }
                return "$" + sDisplay;
            }

            public void AddCash(Integer iAmount) {
                iCash += iAmount;
                SaveValue("iCash", iCash);
            }

            public void DeductCash(Integer iAmount) {
                iCash -= iAmount;
                SaveValue("iCash", iCash);
            }

        }////////////////////////
        //////////////////////////////
        class SceneManager {
            public Integer
                    iDistEnCounter
                    ;
            public float
                    fDistCount;
            private Boolean
                    bDemo;
            private Scene3
                    smScene;
            protected PowerUpManager PUM;
            private Balloon bal = new Balloon();

            public void Setup() {
                smScene = new Scene3();
                smScene.Setup();
                bDemo = false;
                iDistEnCounter = 0;
                PUM = new PowerUpManager();
                PUM.Setup();
                bal = new Balloon();
            }
            public void Destroy() {
                PUM.Destroy();
                smScene.Destroy();
            }
            public void Resume() {
            }
            public void Reset(Boolean bDemop) {
                smScene.Reset(bDemop);
                bDemo = bDemop;
                fDistCount = 0;
                iDistEnCounter = 0;
                PUM.Reset();
                bal.Reset();
            }

            public void Update(float fChangerp,float Delta) {
                if (!bPaused && !bDemo) {
                    bal.Update(Delta);
                    smScene.Update(fChangerp);

                    if (!bDemo && !bPaused) {
                        fDistCount += 3;
                        if (fDistCount >= 10) {
                            DC.IncDist(1);
                            fDistCount = 0;
                            iDistEnCounter++;
                        }
                        if (iDistEnCounter == 33) {
                            DC.EnDist();
                            iDistEnCounter = 0;
                            SK.IncScore(10);
                            pYou.EngdScore();
                        }
                        PUM.Update(fChangerp, 0);
                    }

                }
            }

            public void DrawScene(SpriteBatch batch1) {
                smScene.Draw(batch1);
                bal.DrawBehind(batch1);
                PUM.Draw(batch1);
            }

            class Scene2 {
                private float
                        fWidth,
                        fHeight,
                        fSpeedMod,
                        fResetPos,
                        f2ndImgRight,
                        fLastRight;
                private Rect
                        rPos[];
                public Integer
                        iLAstimgPos,
                        iSceneNo;
                private Boolean
                        bEnding,
                        bVis;
                private Integer
                        img1Place,
                        img2Place;
                public void Resume(float fWidthp, float fHeightp) {
                    fWidth = fWidthp;
                    fHeight = fHeightp;
                }
                public void Setup(Integer iImg1Placep,Integer iImg2Placep,float fWidthp, float fHeightp, float fSpeedModp,Integer iSceneNoP) {
                    fWidth = fWidthp;
                    fHeight = fHeightp;
                    fSpeedMod = fSpeedModp;
                    f2ndImgRight = 0;
                    bEnding = false;
                    img1Place = iImg1Placep;
                    img2Place = iImg2Placep;
                    bVis = false;
                    iSceneNo = iSceneNoP;

                }

                public void EndAbrupt() {
                    bEnding = true;
                    bVis = false;
                    for (Rect r : rPos) {
                        r.left = ScreenX * 2;
                        r.right = (r.left + fWidth);
                    }
                }

                public void Start(float fStartPosP) {
                    rPos = new Rect[2];
                    for (int i = 0; i < rPos.length; i++) {
                        rPos[i] = new Rect();
                    }
                    for (int i = 0; i < 2; i++) {
                        rPos[i].left = (fStartPosP + (i * fWidth));
                        rPos[i].right = (rPos[i].left + fWidth);
                        rPos[i].top = (fHeight);
                        rPos[i].bottom = 0;
                    }
                    fResetPos = fWidth;
                    fLastRight = 0;
                    f2ndImgRight = 0;
                    bEnding = false;
                    iLAstimgPos = 1;
                    bVis = true;
                         /*iNorm = 0,
                            iWTF = 1,
                            iIce = 2,
                            iPrem = 3;*/

                }

                public void EndScene() {
                    bEnding = true;
                }

                public void Update(float fChangeP) {
                    if (bVis) {
                        fLastRight = -1000;
                        if (!bPaused) {
                            for (Rect rPo : rPos) {
                                rPo.left -= (fChangeP * fSpeedMod);
                                rPo.right = (rPo.left + fWidth);
                                rPo.top = bDemo ? rBar.top : ScreenY;
                                rPo.bottom = bDemo ? rBar.bottom : 0;
                                if (rPo.right > fLastRight) fLastRight = rPo.right;
                            }
                        }
                        f2ndImgRight = rPos[iLAstimgPos].right;

                        if (!bEnding && bVis) {
                            for (Rect Ra : rPos) {
                                if (Ra.right < 0) {
                                    Ra.left = (fResetPos);
                                    Ra.right = (Ra.left + fWidth);
                                }
                            }
                        }
                        if (f2ndImgRight < 0) {
                            bEnding = true;
                        }

                        if (fLastRight < 0 && bEnding) {
                            bVis = false;
                            for (Rect r : rPos) {
                                r.left = ScreenX * 2;
                                r.right = (r.left + fWidth);
                            }
                        }
                    }
                }

                public void Draw(SpriteBatch batch) {
                }

            } //Redundant

            class Scene3 {
                private Integer iRectsPassed,iLevelCount,iBkgImageNumber[];
                private float fBkgWidth;
                private Rect rBackground[];
                private Texture imgBackground[];

                public void Setup() {
                    rBackground = new Rect[30];
                    for (int i = 0; i < rBackground.length; i++) {
                        rBackground[i] = new Rect();
                        rBackground[i].OffScreen();
                    }
                    fBkgWidth = 3000;
                    iRectsPassed = 0;
                    iLevelCount = 0;
                    imgBackground = new Texture[12];
                    imgBackground[0] = new Texture("Backgrounds/oned.jpg");
                    imgBackground[1] = new Texture("Backgrounds/twod.jpg");
                    imgBackground[2] = new Texture("Backgrounds/threed.jpg");
                    imgBackground[3] = new Texture("Backgrounds/fourd.jpg");
                    imgBackground[4] = new Texture("Backgrounds/BkgPink.jpg");
                    imgBackground[5] = new Texture("Backgrounds/BkgDino.jpg");
                    imgBackground[6] = new Texture("Backgrounds/BkgBlack.jpg");
                    imgBackground[7] = new Texture("Backgrounds/BkgBlacktoRed.jpg");
                    imgBackground[8] = new Texture("Backgrounds/IceMnt.jpg");
                    imgBackground[9] = new Texture("Backgrounds/imgFinal.jpg");
                    imgBackground[10] = new Texture("Backgrounds/fived.jpg");
                    imgBackground[11] = new Texture("Backgrounds/sixd.jpg");
                    iBkgImageNumber = new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,4,5,6,0,1,2,3,4,5,6,7,8,9,10,11,4,5,6};
                }
                public void Reset(Boolean bDemo) {
                    for (int i = 0; i < rBackground.length; i++) {
                        rBackground[i].left = (i * fBkgWidth);
                        rBackground[i].top =ScreenHeight;
                        rBackground[i].right = rBackground[i].left + fBkgWidth;
                        rBackground[i].bottom = 0;
                    }
                    iRectsPassed = 0;
                    iBkgImageNumber = !bDemo?
                    new Integer[]{0,1,2,3,4,5,6,7,8,9,10,11,4,5,6,0,1,2,3,4,5,6,7,8,9,10,11,4,5,6}  :
                    new Integer[]{0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1};
                }
                public void Update(float fChangep) {
                    for (int i = 0; i < rBackground.length; i++) {
                        rBackground[i].left -= fChangep*1.5f;
                        rBackground[i].right = rBackground[i].left + fBkgWidth;
                        if (rBackground[i].right < 0) {
                            float fBehind = 0;
                            for (int j = 0; j < rBackground.length; j++) {
                                if (rBackground[j].right > fBehind) fBehind = rBackground[j].right;
                            }
                            rBackground[i].left = fBehind;
                            rBackground[i].right = rBackground[i].left + fBkgWidth;
                            iRectsPassed++;
                            iLevelCount = Math.round(iRectsPassed);
                            switch (iLevelCount) {
                                case 0:
                                    brBoxes.iFollow = 0;
                                    break;
                                case 3:
                                    brBoxes.iFollow = 1;
                                    break;
                                case 6:
                                    brBoxes.iFollow = 2;
                                    break;
                                case 9:
                                    brBoxes.iFollow = 3;
                                    break;
                                case 12:
                                    brBoxes.iFollow = 3;
                                    break;
                                case 15:
                                    brBoxes.iFollow = 4;
                                    break;
                            }
                        }
                    }

                }

                public void Draw(SpriteBatch batch1) {
                    for (int j = 0; j < rBackground.length; j++) {
                        DrawToRect(rBackground[j],imgBackground[iBkgImageNumber[j]],batch1);
                    }
                }

                public void Destroy() {
                    for (int i = 0; i < imgBackground.length; i++) {
                        imgBackground[i].dispose();
                    }
                }

            }

            class PowerUpManager {
                protected  final int
                        iShield = 1,
                        iNuke = 2,
                        iBeam = 3
                        ;
                protected Integer iWaitTime,iNewOne;
                protected Texture
                        imgPowerUps[];
                protected PowerUp PU,PU2;
                protected Rect
                        impRect = new Rect();

                protected void Update(float fLeft, float fDown) {
                    if ((iNewOne > iWaitTime)) {
                        if (PU.bHit) {
                            PU.Start(rand.nextInt(3) + 1);
                        } else if (PU2.bHit) {
                            PU2.Start(rand.nextInt(3) + 1);
                        }
                        iNewOne = 0;
                    }
                    PU.Move(fLeft,fDown);
                    PU2.Move(fLeft,fDown);
                    iNewOne++;
                    impRect = pYou.PlayerRect;
                    impRect.top = pYou.PlayerRect.top + pYou.iPlayerSize;
                    impRect.bottom = pYou.PlayerRect.top;
                    PU.ImpactDetect(impRect);
                    PU2.ImpactDetect(impRect);
                }
                public void Destroy() {
                    for (int i = 1; i < imgPowerUps.length; i++) {
                        imgPowerUps[i].dispose();

                    }
                }
                protected void Setup() {
                    imgPowerUps = new Texture[3];
                    imgPowerUps = new Texture[]{null,new Texture("Weaponry/shield.png"),new Texture("Weaponry/Nuke.png"),new Texture("Weaponry/Beam.png")};
                    PU = new PowerUp();
                    PU2 = new PowerUp();
                    PU.Setup(1.8f,0,Math.round(10*iUnit));
                    PU2.Setup(2f,0,Math.round(10*iUnit));
                }
                public void Reset() {
                    PU.Reset();
                    PU2.Reset();
                    iWaitTime = (rand.nextInt(3000) + 3000) / (SK.iDiffLevel + 1);
                    iNewOne = 0;
                }
                public void Draw(SpriteBatch batch) {
                    if (!bDemo) {
                        PU.Draw(batch);
                        PU2.Draw(batch);
                    }
                }

                class PowerUp {
                    private Rect
                            rPU = new Rect();
                    private Integer
                            iWidth,
                            iType;
                    private float fSpeedVert,fSpeedHor;
                    public Boolean
                            bVisible,bHit;

                    public void Setup(float fSpeedHorP,float fSpeedVertp, Integer iWidthp ) {
                        bVisible = false;
                        fSpeedHor = fSpeedHorP;
                        fSpeedVert = fSpeedVertp;
                        iWidth = iWidthp;
                    }

                    public void Reset() {
                        rPU.left = ScreenWidth;
                        rPU.right = rPU.left + iWidth;
                        rPU.top = rand.nextInt(Math.round(ScreenHeight - Math.round(10*iUnit))) + Math.round(10*iUnit);
                        rPU.bottom = rPU.top - iWidth;
                        bHit = true;
                    }
                    public void Start(Integer iTypep) {
                        iType = iTypep;
                        bHit = false;
                    }
                    public void Move(float iLeft, float iDown) {
                        if (!bPaused && !bHit) {
                            rPU.left -= iLeft * fSpeedHor;
                            rPU.right = rPU.left + iWidth;
                            rPU.bottom -= iDown * fSpeedVert;
                            rPU.top = rPU.bottom + iWidth;
                            bVisible = rPU.right > 0 && rPU.left < ScreenWidth;
                        }
                        if (rPU.right < 0) Reset();
                    }
                    public void ImpactDetect(Rect rImpact) {
                        if ((rImpact.right > rPU.left && rImpact.left < rPU.right) && (rImpact.bottom < rPU.top && rImpact.top > rPU.bottom)) {
                            switch (iType) {
                                case iNuke:
                                    brBoxes.Nuke();
                                    iNuked = 90;
                                    break;
                                case iShield:
                                    pYou.bShieldActive = true;
                                    break;
                                case iBeam:
                                    pYou.ShootBeam();
                                    break;
                            }
                            Reset();
                        }
                    }
                    public void Draw(SpriteBatch batch1) {
                        if (bVisible && !bHit) {
                            DrawToRect(rPU,imgPowerUps[iType],batch1);
                        }
                    }
                }
            }

            class Balloon {
                public final int
                        iBalloon1 = 1,
                        iBalloon2 = 2,
                        iCloud = 3,
                        iPeng = 4;
                private Texture
                        imgBalloon1,
                        imgBalloon2,
                        imgCloud,
                        imgPeng;
                private Boolean
                        bCloud,
                        bB1,
                        bB2,
                        bPeng;
                private SmallerBalloon
                        sB1[];
                private float fWidthb,
                        fHeightb;
                private Integer iCount;

                public Balloon() {
                    imgCloud = new Texture("Backgrounds/cloud.png");
                    //imgBalloon1 = new Texture("Backgrounds/Baloon.png");
                    //imgBalloon2 = new Texture("Backgrounds/BaloonYellow.png");
                    imgPeng = new Texture("Backgrounds/peng.png");
                    sB1 = new SmallerBalloon[3];
                    for (int i = 0; i < sB1.length;i++) {
                        sB1[i] = new SmallerBalloon(rand.nextBoolean(),3,rand.nextInt(10) + 5);
                    }
                }
                public void Destroy() {
                    imgCloud.dispose();
                    imgBalloon1.dispose();
                    imgBalloon2.dispose();
                    imgPeng.dispose();
                }
                public void Update(float Delta) {
                    for (int i = 0; i < sB1.length;i++) {
                        sB1[i].Update(true,Delta);
                    }
                }

                public void DrawBehind(SpriteBatch batch1) {
                    for (int i = 0; i < sB1.length;i++) {
                        //sB1[i].DrawBehind(batch1);
                    }
                }

                public void DrawInfront(SpriteBatch batch1) {
                    for (int i = 0; i < sB1.length;i++) {
                        //sB1[i].DrawInfront(batch1);
                    }

                }

                public void Reset() {
                    for (int i = 0; i < sB1.length;i++)
                        sB1[i].Reset();
                }


                class SmallerBalloon {
                    public Integer
                            iType, iAngle2;
                    private float fSpeed;
                    public final int
                            iBalloon1 = 1,
                            iBalloon2 = 2,
                            iCloud = 3,
                            iPeng = 4;
                    private Rect
                            rBalloon = new Rect(),
                            rTest = new Rect();
                    private Boolean
                            bShowing,
                            bInfront;

                    public SmallerBalloon(Boolean bInfrontp, Integer iTypep,float fSpeedp) {
                        bInfront = bInfrontp;
                        iType = iCloud;
                        fSpeed = fSpeedp;
                    }

                    public void Update(Boolean bShowing, float Delta) {
                        rTest = rBalloon;
                        if (rTest.right < 0 || rTest.left > ScreenWidth || rTest.top < 0 || rTest.bottom > ScreenHeight) {
                            if (iCount > 300)Reset();
                            iCount++;
                        }
                        rBalloon.left += (Math.cos(Math.toRadians(iAngle2)) * (fSpeed*iUnit) * 0.02f * Delta);
                        rBalloon.bottom += (Math.sin(Math.toRadians(iAngle2)) * (fSpeed*iUnit) * 0.02f * Delta);
                        if (iType == iCloud) {
                                    rBalloon.right =  rBalloon.left + fWidthb;
                                    rBalloon.top = rBalloon.bottom + fHeightb;
                        } else {
                            rBalloon.right = rBalloon.left + (8*iUnit);
                            rBalloon.top = rBalloon.bottom + (10 * iUnit);
                        }
                    }

                    public void DrawBehind(SpriteBatch batch1) {
                        if (!bInfront) switch (iType) {
                            case iBalloon1:
                                DrawToRect(rBalloon, imgBalloon1, batch1);
                                break;
                            case iBalloon2:
                                DrawToRect(rBalloon, imgBalloon2, batch1);
                                break;
                            case iCloud:
                                DrawToRect(rBalloon, imgCloud, batch1);
                                break;
                            case iPeng:
                                DrawToRect(rBalloon, imgPeng, batch1);
                                break;
                        }
                    }

                    public void DrawInfront(SpriteBatch batch1) {
                        if (bInfront) switch (iType) {
                            case iBalloon1:
                                DrawToRect(rBalloon, imgBalloon1, batch1);
                                break;
                            case iBalloon2:
                                DrawToRect(rBalloon, imgBalloon2, batch1);
                                break;
                            case iCloud:
                                DrawToRect(rBalloon, imgCloud, batch1);
                                break;
                            case iPeng:
                                DrawToRect(rBalloon, imgPeng, batch1);
                                break;
                        }
                    }

                    public void Reset() {
                        rBalloon.OffScreen();
                        iType = iCloud;
                        if (iType == iCloud) {
                            switch (rand.nextInt(2)) {
                                case 0:
                                    rBalloon.left = -(ScreenX);
                                    rBalloon.top = ScreenY;
                                    rBalloon.right = 0;
                                    rBalloon.bottom = 0;
                                    iAngle2 = 200 + rand.nextInt(50);
                                    break;
                                case 1:
                                    rBalloon.left = ScreenX;
                                    rBalloon.top = ScreenY;
                                    rBalloon.right = (ScreenX*2);
                                    rBalloon.bottom = 0;
                                    iAngle2 = 91 + rand.nextInt(90);
                                    break;
                            }
                            fWidthb = rBalloon.right - rBalloon.left;
                            fHeightb = rBalloon.top - rBalloon.bottom;
                        } else {
                            rBalloon.left =  ScreenX ;
                            rBalloon.top = (20*iUnit);
                            rBalloon.right = rBalloon.left + (18*iUnit);
                            rBalloon.bottom = rBalloon.top - (20*iUnit);
                            iAngle2 = 91 + rand.nextInt(90);
                        }
                        iCount = 0;
                        fSpeed = rand.nextInt(30) + 10;
                    }
                }
            }
        }///////////////////////
        //////////////////////////////
        class BackgroundManager {
        /*private Scroller
                bkgObjs[],
                ScrlBKG
                ;

        public void ChangeScene(String sScene) {

        }
        public void MoveLeft(float fChange) {
            ScrlBKG.MoveLeft(fChange);
        }

        public void Setup() {
            ScrlBKG = new Scroller();
            Bitmap bmp[] = new Bitmap[3];
            bmp[0] = BitmapFactory.decodeResource(res,R.drawable.bggrey,opts);
            bmp[1] = BitmapFactory.decodeResource(res,R.drawable.bggreen2,opts);
            bmp[2] = BitmapFactory.decodeResource(res,R.drawable.bgleafy4,opts);
            Integer iHeight[] = new Integer[]{ScreenY,ScreenY,ScreenY};
            ScrlBKG.Setup(bmp,255,Math.round(100*iUnit),ScreenY,0,true);
            ScrlBKG.StartScroll(0,ScreenY,-1.0f,0);
        }

        public void ResetBKG() {
            ScrlBKG.StartScroll(0,ScreenY,-1.0f,0);
        }

        public void DrawBKG(Canvas canv) {
            ScrlBKG.DrawScroller(canv);
        }

       public void Destroy() {
           ScrlBKG.endScroll();

       }

        class Scroller {
            private float
                    fSpeedHor,
                    fSpeedVert
                    ;
            private Bitmap
                    imgArray[]
                    ;
            private Integer
                    iRespawnDist,
                    iGap,
                    iBottom,
                    iWidth,
                    iHeight
                    ;
            private Rect
                    rImage[]
                    ;
            private Paint
                    BKGPaint = new Paint()
                    ;
            private boolean
                    bEnded = false;
                    ;
            public Integer iLastRight;
            public Boolean
                   bHorizontalScroll,
                   bPaused = false;

            public void endScroll() {
                bEnded = true;
                bPaused = true;
                for (Bitmap anImgArray : imgArray) {
                    anImgArray.recycle();
                }
            }

            public void StartScroll(Integer iStartP,Integer iBottomP,float fSpeedHorP,float fSpeedVertP) {
                bPaused = false;

                iBottom = iBottomP;
                fSpeedHor = fSpeedHorP;
                fSpeedVert = fSpeedVertP;
                for (int i = 0; i < imgArray.length; i++) {
                    rImage[i].left = iStartP + (i*iWidth) + (i*iGap);
                    rImage[i].right = iStartP + ((i*iWidth) + (i*iGap) + iWidth);
                    rImage[i].top = iBottomP - iHeight;
                    rImage[i].bottom = iBottomP;
                }
            }

            public void MoveLeft(float fChange2) {
                iLastRight = 0;
                for (Rect aRImage : rImage) {
                    if (!bPaused) {
                        aRImage.left = aRImage.left + Math.round(fChange2 * fSpeedHor);
                        aRImage.right = aRImage.left + iWidth;
                        aRImage.bottom = aRImage.bottom + Math.round(fChange2 * fSpeedVert);
                        aRImage.top = aRImage.bottom - iHeight;
                    }
                    if (aRImage.right < 0 && !bEnded) {
                        aRImage.left = iRespawnDist - Math.round(5 * iUnit);
                        aRImage.right = aRImage.left + iWidth;
                    }
                    if (aRImage.right > iLastRight) iLastRight++;
                }
            }

            public void Setup(Bitmap[] imgArrayP,Integer iTransparency,Integer iWidthP,Integer iHeightP,Integer iGapP,Boolean bHorizontalScrollP) {
                imgArray = imgArrayP;
                iWidth = iWidthP;
                iHeight = iHeightP;
                BKGPaint.setARGB(iTransparency,0,0,0);
                rImage = new Rect[imgArray.length];
                for (int i = 0; i < imgArray.length; i++) {
                    rImage[i] = new Rect();
                }
                iGap = iGapP;
                iWidth = iWidthP;
                bHorizontalScroll = bHorizontalScrollP;
                if (bHorizontalScroll) {
                    iRespawnDist = (iWidth*(imgArray.length - 1))- Math.round(5*iUnit);
                }   else {
                    iRespawnDist = (iHeight*(imgArray.length - 1))- Math.round(5*iUnit);
                }

            }

            private void DrawScroller(Canvas canvas) {
                for (int i = 0; i < rImage.length; i++) {
                    canvas.drawBitmap(imgArray[i],null,rImage[i],BKGPaint);
                }
            }

        }
*/
        }////Redundant/////
        //////////////////////////////
        class TouchLayout {
            private boolean
                    JumpLeft = true;
            private Texture
                    imgJump;
            private Rect
                    rJump = new Rect(),
                    rShoot1 = new Rect(),
                    rLine = new Rect();
            private Integer iOverlayCount;

            public void Setup() {
                JumpLeft = bFlipped;

                rJump.left = 0;
                rJump.right = (rJump.left + (50 * iUnit));
                rJump.top = (ScreenY - 40 * iUnit);
                rJump.bottom = (rJump.top - (50 * iUnit));

                rShoot1.left = (ScreenX);
                rShoot1.right = (ScreenX - (50 * iUnit));
                rShoot1.top = (ScreenY - (40 * iUnit));
                rShoot1.bottom = (rShoot1.top - (50 * iUnit));
                rLine.top = ScreenY;
                rLine.bottom = 0;
                rLine.left = ((ScreenX/2) - (1*iUnit));
                rLine.right = ((ScreenX/2) + (1*iUnit));
                imgJump = new Texture("UI/ThumbLeft.png");
            }
            public void Destroy() {
                imgJump.dispose();
            }
            public void Reset() {
                iOverlayCount = 180;
            }
            public void DrawTouch(SpriteBatch batch1) {
                if (iOverlayCount > 0) {
                    iOverlayCount--;
                    if (iOverlayCount == 105) iOverlayCount = 75;
                    batch1.setColor(1,1,1,(float) Math.sin(Math.toRadians(iOverlayCount)));
                    DrawToRect(rJump,imgJump,batch1);
                    DrawToRect(rShoot1,imgJump,batch1);
                    DrawToRect(rLine,imgBlack,batch1);
                }
            }
        }////////////////////////
        //////////////////////////////
        class player {/////////
            public float
                    PlayerWidth;
            private Texture
                    bmpPlayer,
                    imgCost0,
                    imgCost1,
                    imgCost2,
                    imgCost3,
                    imgCost4,
                    imgCost5,
                    imgCost6,
                    imgCost7,
                    imgShield;
            public Rect
                    PlayerRect = new Rect(),
                    rScoreRect = new Rect(),
                    rActPlayRect = new Rect(),
                    rSunRect = new Rect(),
                    rBeamRect = new Rect();

            public Integer
                    iCostume,
                    iPlayerSize = 7,
                    iEnAngle,
                    iWait,
                    iBeamY,
                    iBeamTimer;
            private Sign
                    sSign[],
                    sDupSign[];
            private Sprite
                    sPlayer;
            private Boolean bEnScore = false,
                    bShieldActive = false,
                    bSun = false;
            private float fScoreSize;

            public void MoveDown(Float fDown) {
                if (!bPaused) {
                    PlayerY -= fDown;
                    if (PlayerY < 0 || PlayerY + PlayerWidth > ScreenY) {
                        OnImpact();
                    }
                }

            }

            public void ShootBeam() {
                iBeamY = Math.round(PlayerY + (iPlayerSize/2 * iUnit));
                iBeamTimer =100;
            }

            private void UpdateBeam(SpriteBatch batch1) {
                if (iBeamTimer > 0 ) {
                    batch.setColor(1,1,1,0.01f * iBeamTimer);
                    DrawToRect(rFullScreen, imgWhite, batch1);
                    rBeamRect.left = PlayerX;
                    rBeamRect.right = ScreenWidth;
                    rBeamRect.top = iBeamY + (0.05f * iBeamTimer * iUnit);
                    rBeamRect.bottom = iBeamY - (0.05f * iBeamTimer* iUnit);
                    batch.setColor(1,1,1,1);
                    DrawToRect(rBeamRect,imgWhite,batch1);
                    iBeamTimer--;
                } else {
                    rBeamRect.top = -100;
                    rBeamRect.bottom =  -100;
                }
            }
            public void setTop(Integer iTop) {
                sPlayer.setPosition(PlayerRect.left,iTop - (10*iUnit));
                PlayerY = iTop - (10*iUnit);
            }

            public void ChangeCostume(Integer iCostumeP) {
                iCostume = iCostumeP;
            }

            public void Reset() {
                PlayerX = 30 * iUnit;
                PlayerY = ScreenY - (35*iUnit);
                PlayerRect.left = (PlayerX);
                PlayerRect.right = (PlayerX + (iPlayerSize * iUnit));
                PlayerRect.top = (PlayerY);
                PlayerRect.bottom = (PlayerY - (iPlayerSize * iUnit));
                PlayerWidth = PlayerRect.right - PlayerRect.left;
                sSign = new Sign[1];
                sSign[0] = new Sign();
                sSign[0].Setup();
                bSun = false;
                switch (iCostume) {
                    case 0:
                        bmpPlayer = imgCost0;
                        break;
                    case 1:
                        bmpPlayer = imgCost1;
                        break;
                    case 2:
                        bmpPlayer = imgCost2;
                        break;
                    case 3:
                        bmpPlayer = imgCost3;
                        break;
                    case 4:
                        bmpPlayer = imgCost4;
                        break;
                    case 5:
                        bmpPlayer = imgCost5;
                        break;
                    case 6:
                        bmpPlayer = imgCost6;
                        break;
                    case 7:
                        bmpPlayer = imgCost7;
                        bSun = true;
                        break;
                }
                sPlayer.setTexture(bmpPlayer);
                sPlayer.setRegion(bmpPlayer);
                sPlayer.setBounds(0, 0, PlayerRect.right - PlayerRect.left, PlayerRect.top - PlayerRect.bottom);
                sPlayer.setOriginCenter();
                sPlayer.setPosition(PlayerX, PlayerY);
                fScoreSize = (0.06f*iUnit);
                iEnAngle = 180;
                bEnScore = false;
                bShieldActive = false;
                iWait = 101;
                iBeamTimer = 0;
                rBeamRect.left = PlayerX;
                rBeamRect.right = ScreenWidth;
                rBeamRect.top = -100;
                rBeamRect.bottom = -100;
            }
            public void EnlScore() {
                bEnScore = true;
                iEnAngle = 0;
            }
            public void EngdScore() {
                bEnScore = false;
                iEnAngle = 0;
            }
            public void Resume() {
                bSun = false;
                switch (iCostume) {
                    case 0:
                        bmpPlayer = imgCost0;
                        break;
                    case 1:
                        bmpPlayer = imgCost1;
                        break;
                    case 2:
                        bmpPlayer = imgCost2;
                        break;
                    case 3:
                        bmpPlayer = imgCost3;
                        break;
                    case 4:
                        bmpPlayer = imgCost4;
                        break;
                    case 5:
                        bmpPlayer = imgCost5;
                        break;
                    case 6:
                        bmpPlayer = imgCost6;
                        break;
                    case 7:
                        bmpPlayer = imgCost7;
                         bSun = true;
                        break;
                }
                sPlayer.setTexture(bmpPlayer);
                sPlayer.setPosition(PlayerX, PlayerY);
            }
            public void Setup() {
                PlayerRect = new Rect();
                iCostume = SK.iPlayerLevel;
                PlayerX = 30 * iUnit;
                PlayerY = 1f;
                PlayerRect.left = (PlayerX);
                PlayerRect.right = (PlayerX + (iPlayerSize * iUnit));
                PlayerRect.top = (PlayerY);
                PlayerRect.bottom = (PlayerY + (iPlayerSize * iUnit));
                PlayerWidth = PlayerRect.right - PlayerRect.left;
                imgCost0 = new Texture("Costumes/player.png");
                imgCost1 = new Texture("Costumes/playerstraw1.png");
                imgCost2 = new Texture("Costumes/playerbounceball.png");
                imgCost3 = new Texture("Costumes/playergirbil2.png");
                imgCost4 = new Texture("Costumes/playerguitarbounce.png");
                imgCost5= new Texture("Costumes/hacker.png");
                imgCost6= new Texture("Costumes/samuraiface.png");
                imgCost7= new Texture("Costumes/GodFace.png");
                imgShield= new Texture("Weaponry/shieldcover.png");
                switch (iCostume) {
                    case 0:
                        bmpPlayer = imgCost0;
                        break;
                    case 1:
                        bmpPlayer = imgCost1;
                        break;
                    case 2:
                        bmpPlayer = imgCost2;
                        break;
                    case 3:
                        bmpPlayer = imgCost3;
                        break;
                    case 4:
                        bmpPlayer = imgCost4;
                        break;
                    case 5:
                        bmpPlayer = imgCost5;
                        break;
                    case 6:
                        bmpPlayer = imgCost6;
                        break;
                    case 7:
                        bmpPlayer = imgCost7;
                        break;
                }
                sSign = new Sign[1];
                sSign[0] = new Sign();
                sSign[0].Setup();
                sPlayer = new Sprite();
                sPlayer.setTexture(bmpPlayer);
                sPlayer.setRegion(bmpPlayer);
                sPlayer.setBounds(0, 0, PlayerRect.right - PlayerRect.left, PlayerRect.right - PlayerRect.left);
                sPlayer.setOriginCenter();
                sPlayer.setPosition(PlayerX, PlayerY);
            }

            public void ShowSign(String sText, float fX, float fY, float fAngle) {
                sDupSign = sSign; //Shoot Bullet
                sSign = new Sign[sSign.length + 1];
                System.arraycopy(sDupSign, 0, sSign, 0, sDupSign.length);
                sSign[sSign.length - 1] = new Sign();
                sSign[sSign.length - 1].Setup();
                sSign[sSign.length - 1].ShowSign(sText, fX, fY, fAngle);
            }

            public void DeprecateSign(Integer i) {
                sDupSign = sSign;
                sSign = new Sign[sSign.length - 1];
                for (int j = 0; j < sSign.length; j++) {
                    if (j < i) {
                        sSign[j] = sDupSign[j];
                    } else {
                        sSign[j] = sDupSign[j + 1];
                    }
                }
            }

            public void OnImpact() {
                if (!bShieldActive) {
                    long eVib[] = new long[50];
                    for (int i = 0; i < 50; i++) {
                        eVib[i] = i;
                    }
                    if (bVibrate) Gdx.input.vibrate(eVib, -1);
                    iGamemode = 3;
                    UpdateGame();

                } else {
                    bShieldActive = false;
                }
            }
            public void DrawScore(SpriteBatch batch1) {
                if (iEnAngle < 180) iEnAngle += 10;
                nfnt.setScale((0.1f + (0.01f * (float)Math.sin(Math.toRadians(iEnAngle)))) *iUnit);
                nfnt.getBounds(SK.GetScore().toString(), 0, SK.GetScore().toString().length(), tb);
                nfnt.setColor(0, 0, 0, 0.5f);
                nfnt.draw(batch1, SK.GetScore().toString(), (ScreenX/2)  - (tb.width / 2) + (0.5f * iUnit), (75* iUnit) + (tb.height / 2) - (0.5f * iUnit));
                 if (bEnScore) nfnt.setColor(1, 1 - (float)Math.sin(Math.toRadians(iEnAngle)), 1 - (float)Math.sin(Math.toRadians(iEnAngle)), 0.8f);
                else nfnt.setColor(1 - (float) Math.sin(Math.toRadians(iEnAngle)), 1, 1 - (float) Math.sin(Math.toRadians(iEnAngle)), 0.8f);
                nfnt.draw(batch1, SK.GetScore().toString(),  (ScreenX/2)  - (tb.width / 2), (75* iUnit) + (tb.height / 2));
            }

            public void DrawPlayer(SpriteBatch batch) {
                UpdateBeam(batch);
                PlayerRect.top = (PlayerY);
                PlayerRect.bottom = (PlayerY - (iPlayerSize * iUnit));
                rActPlayRect.left = PlayerX - (20*iUnit);
                rActPlayRect.right = PlayerX + (iPlayerSize * iUnit) + (20*iUnit);
                rActPlayRect.bottom = PlayerY - (20*iUnit);
                rActPlayRect.top = PlayerY + (iPlayerSize * iUnit) + (20*iUnit);
                if (bSun) DrawToRect(rActPlayRect, imgSun, batch);
                sPlayer.draw(batch);
                sPlayer.setPosition(PlayerX,PlayerY);
                if (bShieldActive) {
                    rActPlayRect.left = PlayerX - (2*iUnit);
                    rActPlayRect.right = PlayerX + (iPlayerSize * iUnit) + (2*iUnit);
                    rActPlayRect.bottom = PlayerY - (2*iUnit);
                    rActPlayRect.top = PlayerY + (iPlayerSize * iUnit) + (2*iUnit);
                    DrawToRect(rActPlayRect,imgShield,batch);

                }

            }

            public void DrawSign(SpriteBatch batch) {
                for (int i = 1; i < sSign.length; i++) {
                    sSign[i].Draw(batch);
                    if (!sSign[i].bShowing) {
                        DeprecateSign(i);
                    }
                }
            }

            public void Destroy() {
                for (int i = 0; i < sSign.length; i++) {
                    sSign[i].Destroy();
                }
                bmpPlayer.dispose();
                        imgCost0.dispose();
                        imgCost1.dispose();
                        imgCost2.dispose();
                        imgCost3.dispose();
                        imgCost4.dispose();
                        imgCost5.dispose();
                        imgCost6.dispose();
                        imgCost7.dispose();
                        imgShield.dispose();;
            }

            class Sign {
                private Texture
                        imgSign;
                private Rect
                        rSignRect = new Rect();
                private Boolean
                        bShowing = false;
                private Integer
                        iSignCount = 0;
                private String
                        sText;
                private float
                        fXinc,
                        fYinc;
                private Sprite
                        sSign;
                public void Destroy() {

                }
                public void Setup() {
                    sSign = new Sprite();
                    sSign.setRegion(bmpPlayer);
                    sSign.setBounds(0, 0, 100, 100);
                    sSign.setOriginCenter();
                    sSign.setPosition(30 * iUnit, 1f);
                    bShowing = false;
                }

                public void ShowSign(String sTextP, float fX, float fY, float fAngle) {
                    bShowing = true;
                    rSignRect.left = (fX - (10*iUnit));
                    rSignRect.right = (fX + (10*iUnit));
                    rSignRect.top = (fY - (10*iUnit));
                    rSignRect.bottom = (fY + (10*iUnit));
                    iSignCount = 0;
                    fAngle = (float) Math.toRadians(fAngle);
                    fYinc = (float) Math.sin(fAngle);
                    fXinc = (float) Math.cos(fAngle);
                    sText = sTextP;
                }

                public void Draw(SpriteBatch batchp) {
                    if (bShowing) {
                        if (iSignCount <= 50) {
                            nfnt.setColor(1, 1, 1, iSignCount * (0.02f));
                        }
                        if (iSignCount < 150 && iSignCount > 50) {
                            nfnt.setColor(1, 1, 1, 1 - ((iSignCount - 50) * 0.01f));
                        }
                        if (iSignCount >= 150) {
                            bShowing = false;
                            nfnt.setColor(1, 1, 1,0);
                        }
                        nfnt.setScale(0.05f*iUnit);
                        nfnt.draw(batchp, sText, rSignRect.left, rSignRect.top);
                        if (iGamemode == 4) {
                            if (!bPaused) {
                                iSignCount += 2;
                                rSignRect.top -= (fXinc * iUnit);
                                rSignRect.bottom -= (fXinc * iUnit);
                                rSignRect.left -= (fYinc * iUnit);
                                rSignRect.right -= (fYinc * iUnit);
                            }
                        } else {
                            iSignCount += 2;
                            rSignRect.top -= (fXinc * iUnit);
                            rSignRect.bottom -= (fXinc * iUnit);
                            rSignRect.left -= (fYinc * iUnit);
                            rSignRect.right -= (fYinc * iUnit);
                        }
                    }
                }
            }
        }/////////////////////////////
        //////////////////////////////
        class DistanceCounter {
            private Integer
                    iDist,
                    iHighDist,
                    iColour,
                    iDistAng;
            private float
                    fDistMod
                    ;
            private BitmapFont.TextBounds tb2;
            private String sDisplay,sDisplay2;
            private Boolean bHighDist;

            public void Setup() {
                iHighDist = SK.GetValue("iHighDist");
                iDist = 0;
                tb2 = new BitmapFont.TextBounds();
                iDistAng = 180;

            }
            public String GetDistS() {
                sDisplay = "";
                if (iDist <= 9) {
                    sDisplay = iDist + "mm ";
                } else if (iDist <= 999) {
                    sDisplay = (Math.round((iDist*0.1f)*100f)/100f) + "cm ";
                } else {
                    sDisplay = (Math.round((iDist*0.001f)*100f)/100f) + "m ";
                }
                return  sDisplay;
            }
            public String GetHighDistS() {
                sDisplay2 = "";
                if (iHighDist <= 9) {
                    sDisplay2 += "(" + iHighDist + "mm)";
                } else if (iHighDist <= 999) {
                    sDisplay2 += "(" + (Math.round((iHighDist*0.1f)*100f)/100f) + "cm)";
                } else {
                    sDisplay2 += "(" + (Math.round((iHighDist*0.001f)*100f)/100f) + "m)";
                }
                return sDisplay2;
            }

            public void EnDist() {
                iDistAng = 0;
            }
            public void DrawDist(SpriteBatch batch1) {
                sDisplay = "";
                if (iDist <= 9) {
                    sDisplay = iDist + "mm ";
                } else if (iDist <= 999) {
                    sDisplay = (Math.round((iDist*0.1f)*100f)/100f) + "cm ";
                } else {
                    sDisplay = (Math.round((iDist*0.001f)*100f)/100f) + "m ";
                }
                sDisplay2 = "";
                if (iHighDist <= 9) {
                    sDisplay2 += "(" + iHighDist + "mm)";
                } else if (iHighDist <= 999) {
                    sDisplay2 += "(" + (Math.round((iHighDist*0.1f)*100f)/100f) + "cm)";
                } else {
                    sDisplay2 += "(" + (Math.round((iHighDist*0.001f)*100f)/100f) + "m)";
                }
                if (iDistAng < 180) iDistAng += 12;
                nfnt.setScale((0.03f + (0.01f*(float)Math.sin(Math.toRadians(iDistAng)))) *iUnit);
                nfnt.getBounds(sDisplay + sDisplay2, 0, (sDisplay + sDisplay2).length(), tb);
                nfnt.setColor(0, 0, 0, 0.5f);
                nfnt.draw(batch,sDisplay, (10 * iUnit) + (0.5f * iUnit) - (2*iUnit*(float)Math.sin(Math.toRadians(iDistAng))), ScreenHeight - (10 * iUnit) - (0.5f * iUnit));
                nfnt.setColor(1, 1, 1, 0.8f);
                nfnt.draw(batch,sDisplay,  (10 * iUnit)- (2*iUnit*(float)Math.sin(Math.toRadians(iDistAng))), ScreenHeight - (10 * iUnit));

                nfnt.setScale(0.03f*iUnit);
                nfnt.getBounds(sDisplay , 0, (sDisplay).length(), tb2);

                nfnt.setColor(0, 0, 0, 0.5f);
                nfnt.draw(batch,sDisplay2, (10 * iUnit) + (0.5f * iUnit) + tb2.width, ScreenHeight - (10 * iUnit) - (0.5f * iUnit));
                if (bHighDist) {
                    iColour += 2;
                    if (iColour == 180) iColour = 0;
                    nfnt.setColor(1- (float) Math.sin(Math.toRadians(iColour)),1 , 1- (float) Math.sin(Math.toRadians(iColour)), 0.8f);
                } else {
                    nfnt.setColor(1, 1, 1, 0.8f);
                }
                nfnt.draw(batch,sDisplay2,  (10 * iUnit) + tb2.width,  ScreenHeight - (10 * iUnit));
                Integer iCash = SK.GetCash() + SK.GetScore();
                sDisplay2 = "" + (iCash);
                sDisplay = "";
                Integer iSpace = 0;
                for (int i = sDisplay2.length() - 1; i >= 0 ; i--) {
                    sDisplay = "" + sDisplay2.charAt(i) + sDisplay;
                    iSpace++;
                    if (iSpace == 3) {
                        sDisplay = " " + sDisplay;
                        iSpace = 0;
                    }
                }
                sDisplay2 = "$" + sDisplay;
                nfnt.setScale(0.03f*iUnit);
                nfnt.getBounds(sDisplay2, 0, (sDisplay2).length(), tb2);
                nfnt.setColor(0, 0, 0, 0.5f);
                nfnt.draw(batch,sDisplay2, (10 * iUnit) + (0.5f * iUnit), ScreenHeight - (13 * iUnit) - (0.5f * iUnit) - tb.height);
                nfnt.setColor(1, 1, 1, 0.8f);
                nfnt.draw(batch,sDisplay2,  (10 * iUnit), ScreenHeight - (13 * iUnit) - tb.height);

            }
            public void Savehigh() {
                SK.SaveValue("iHighDist",iHighDist);
            }
            public void IncDist(Integer iAmount) {
                iDist += iAmount;
                if (iDist > iHighDist) {
                    iHighDist = iDist;
                    bHighDist = true;
                }
            }
            public void Reset() {
                iDist = 0;
                bHighDist = false;
                iColour = 0;
                Savehigh();
            }
        }////////////////////
        //////////////////////////////
        class UserSpace {
            private Rect
                    rPlay,
                    rBKG;

            public Texture
                    imgButtonBKG,
                    imgBtnPressed,
                    imgBar,
                    imgBarBot;
            private Boolean
                    bPlayTouch;

            public void Setup() {
                rPlay = new Rect();
                rBKG = new Rect();
                //Top bar                       BARS
                UpdateRects();
                //Play button                   BUTTONS
                rPlay.top = (20 * iUnit);
                rPlay.left = ((ScreenWidth/2) - (20*iUnit));
                rPlay.right = ((ScreenWidth/2) + (20*iUnit));
                rPlay.bottom = rPlay.top - (15 * iUnit);
                imgButtonBKG = new Texture("UI/btnbevel.png");
                imgBtnPressed = new Texture("UI/btnbevelpressed.png");
                imgBar = new Texture("UI/greybar.jpg");
                //rDebug
                bPlayTouch = false;
            }
            private void UpdateRects() {
                rTopBar.top = ScreenY + (float)(Math.sin(Math.toRadians(iDownSinLevel)) * rTopBar.height());
                rTopBar.left = 0;
                rTopBar.right = ScreenX;
                rTopBar.bottom = ScreenY - (15 * iUnit) + (float)(Math.sin(Math.toRadians(iDownSinLevel)) * rTopBar.height());
            }
            public void DrawSpace(SpriteBatch batch1) {
                DrawBars(batch1);
                DrawToRect(rPlay, bPlayTouch ? imgBtnPressed :imgButtonBKG, batch1);
                nfnt.setScale(0.04f * iUnit);
                nfnt.setColor(bPlayTouch ? 0.4f : 1,1, bPlayTouch ? 0.4f : 1, 1);
                nfnt.getBounds("Start",0,5,tb);
                nfnt.draw(batch1,"Start",rPlay.left + (((rPlay.right - rPlay.left) - (tb.width))/2), rPlay.top - (((rPlay.top - rPlay.bottom) - (tb.height))/2));
                fnt.setScale(0.04f * iUnit);
                fnt.setColor(1,1,1,7f);
                fnt.getBounds("HighScore     " + SK.GetHighScore().toString(),tb);
                fnt.draw(batch1, "HighScore         " + SK.GetHighScore().toString(), ScreenX / 2 - (tb.width/2), 70*iUnit);
                fnt.draw(batch1, "Best distance   " + DC.GetHighDistS(), ScreenX / 2 - (tb.width/2), 60*iUnit);
            }

            public void DrawBars(SpriteBatch batch1) {
                UpdateRects();
                DrawToRect(rTopBar,imgBar,batch1);
            }

            public void ReleaseDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = rPlay;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    iGamemode = 5;
                    UpdateGame();
                }
                bPlayTouch = false;
            }

            public void touchDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = rPlay;
                bPlayTouch = (fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom);
            }

            public void Destroy() {
                imgButtonBKG.dispose();
                        imgBtnPressed.dispose();
                        imgBar.dispose();
            }
        }//////////////////////////
        //////////////////////////////
        class UpgradeSpace {
            private Integer
                    iLGunLevel,
                    iGunOpts,
                    iLIGLevel,
                    iLPlayerLevel,
                    iLRadiusLevel,
                    iPlayPrice[] = new Integer[]{0, 500, 1000, 2000, 3000, 4000,6000,10000},
                    iRPrice[] = new Integer[]{0, 0, 0},
                    iPPrice[] = new Integer[]{0, 100, 500, 1000},
                    iSPrice[] = new Integer[]{0, 500, 1000, 1500},
                    iBePrice[] = new Integer[]{8000, 0},
                    iBaPrice[] = new Integer[]{1000, 2000, 4000, 10000},
                    iRadiusPrice[] = new Integer[]{0, 100, 400, 800, 1500, 2000, 3000, 4000, 5000, 8000, 10000};
            private Rect
                    rPlay = new Rect(),
                    rBack = new Rect(),
                    rDisplayLeft = new Rect(),
                    rDisplayLockLeft = new Rect(),
                    rDisplayLockMid = new Rect(),
                    rDisplayLockRight = new Rect(),
                    rDisplayMid = new Rect(),
                    rDisplayright = new Rect(),
                    rCashDisp = new Rect(),
                    rInfButton = new Rect(),
                    rMid = new Rect(),
                    rRight = new Rect(),
                    rPlayer = new Rect(),
                    rT = new Rect();
            private FS
                    scrl = new FS(),
                    scrl2 = new FS(),
                    scrl3 = new FS();
            public Texture
                    imgLocked,
                    imgUnlock,
                    imgsliderBack,
                    imgSliderFront,
                    imgRand,
                    imgPistol,
                    imgShotgun,
                    imgBazooka,
                    imgBeam,
                    imgInfo,
                    imgInfobtn,
                    imgCashBKG,
                    imgWhiteCircle,
                    imgGreyBox
                            ;
            public boolean
                    bPlayTouch,
                    bBackTouch,
                    bDrawInf,
                    bSlidingDown;
            private character
                    ch[];
            private Sprite
                    sWhiteCircle = new Sprite();
            private float fBottom = 0;

            public void dispose() {
                imgLocked.dispose();
                        imgUnlock.dispose();
                        imgsliderBack.dispose();
                        imgSliderFront.dispose();
                        imgRand.dispose();
                        imgPistol.dispose();
                        imgShotgun.dispose();
                        imgBazooka.dispose();
                        imgBeam.dispose();
                        imgInfo.dispose();
                        imgInfobtn.dispose();
                        imgCashBKG.dispose();
                        imgWhiteCircle.dispose();
            }

            public void Setup() {
                ch = new character[8];
                for (int i = 0; i < ch.length; i++) {
                    ch[i] = new character();
                    ch[i].Setup(i);
                }
               /* rPlay.top = (20 * iUnit);
                rPlay.left = ((ScreenWidth/2) - (20*iUnit));
                rPlay.right = ((ScreenWidth/2) + (20*iUnit));
                rPlay.bottom = rPlay.top - (15 * iUnit);*/

                UpdateRects();

                Rect rOut = new Rect();
                Rect rOut2 = new Rect();
                Rect rOut3 = new Rect();
                rOut.RectCopy(rDisplayLeft);
                rOut.top = rDisplayLeft.bottom - (2 * iUnit);
                rOut.bottom = rOut.top - (8 * iUnit);
                scrl.Setup(rOut, 8, SK.iPlayerLevel,1);
                rOut2.RectCopy(rDisplayMid);
                rOut2.top = rDisplayMid.bottom - (2 * iUnit);
                rOut2.bottom = rOut.top - (8 * iUnit);
                scrl2.Setup(rOut2, iMaxUpgradeLevel + 1, SK.iGunLevel,2);
                rOut3.RectCopy(rDisplayright);
                rOut3.top = rDisplayright.bottom - (2 * iUnit);
                rOut3.bottom = rOut.top - (8 * iUnit);
                scrl3.Setup(rOut3, iMaxUpgradeLevel + 1, SK.iDiffLevel,3);
                iLGunLevel = 0;
                iLIGLevel = iMaxUpgradeLevel;
                iLRadiusLevel = 0;
                iLPlayerLevel = 0;
                LoadRes();
                iLPlayerLevel = SK.iPlayerLevel;
            }

            private void UnloadRes() {
                imgLocked.dispose();
                imgUnlock.dispose();
                imgsliderBack .dispose();
                imgSliderFront.dispose();
                imgRand.dispose();
                imgPistol.dispose();
                imgShotgun.dispose();
                imgBazooka.dispose();
                imgBeam .dispose();
                imgInfo.dispose();
                imgInfobtn.dispose();
                imgCashBKG.dispose();
                imgWhiteCircle.dispose();
                imgGreyBox.dispose();
            }
            private void LoadRes() {
                imgLocked = new Texture("UI/circlelock.png");
                imgUnlock = new Texture("UI/btngreenlock.png");
                imgsliderBack = new Texture("UI/scrollBack.png");
                imgSliderFront = new Texture("UI/scrollFront.png");
                imgRand = new Texture("Weaponry/bsRand.png");
                imgPistol = new Texture("Weaponry/bsPist.png");
                imgShotgun = new Texture("Weaponry/bsShotgun.png");
                imgBazooka = new Texture("Weaponry/bsRocket.png");
                imgBeam = new Texture("Weaponry/bsBeam.png");
                imgInfo = new Texture("UI/Instr.png");
                imgInfobtn= new Texture("UI/infoButton.png");
                imgCashBKG= new Texture("UI/btnnobevel.png");
                imgWhiteCircle= new Texture("UI/WhiteCircle.png");
                imgGreyBox = new Texture("UI/UpgradesDisplayBox.png");
            }
            private void UpdateRects() {
                rPlay.left = ScreenX - (44 * iUnit);
                rPlay.right = rPlay.left + (38 * iUnit);
                rPlay.top = fBottom + (20 * iUnit);
                rPlay.bottom = rPlay.top - (15 * iUnit);

                rBack.left = (6 * iUnit);
                rBack.right = rBack.left + (38 * iUnit);
                rBack.top = fBottom + (20 * iUnit);
                rBack.bottom = rBack.top - (15 * iUnit);




                rDisplayLeft.left = (((ScreenX/100)* 22) - (13 * (ScreenX/100)));
                rDisplayLeft.right = rDisplayLeft.left + (26 * (ScreenX/100));
                rDisplayLeft.top = fBottom + ((ScreenY / 2) + 15 * iUnit);
                rDisplayLeft.bottom = fBottom + ((ScreenY / 2) - 15 * iUnit);

                rDisplayLockLeft.CopySquare(rDisplayLeft,0);

                rDisplayMid.left = (((ScreenX/100)* 53) - (13 * (ScreenX/100)));
                rDisplayMid.right = rDisplayMid.left + (26 * (ScreenX/100));
                rDisplayMid.top = fBottom + ((ScreenY / 2) + 15 * iUnit);
                rDisplayMid.bottom = fBottom + ((ScreenY / 2) - 15 * iUnit);

                rDisplayLockMid.CopySquare(rDisplayMid,0);

                rDisplayright.left = (((ScreenX/100)* 84) - (13 * (ScreenX/100)));
                rDisplayright.right = rDisplayright.left + (26 * (ScreenX/100));
                rDisplayright.top = fBottom + ((ScreenY / 2) + 15 * iUnit);
                rDisplayright.bottom = fBottom + ((ScreenY / 2) - 15 * iUnit);

                rDisplayLockRight.CopySquare(rDisplayright,0);

                rCashDisp.top = rTopBar.top - (3*iUnit);
                rCashDisp.bottom = rCashDisp.top - (8*iUnit);

                rInfButton.left = ((ScreenWidth / 2) - (5 * iUnit));
                rInfButton.right = ((ScreenWidth/2)+(5*iUnit));
                rInfButton.top = fBottom + (12.5f*iUnit);
                rInfButton.bottom = fBottom + (2.5f*iUnit);

                rMid.left = (ScreenX / 3);
                rMid.right = (ScreenX / 3) * 2;
                rMid.top = fBottom + (75 * iUnit);
                rMid.bottom = fBottom + (20 * iUnit);

                rPlayer.left = rMid.left + ((rMid.right - rMid.left) / 4);
                rPlayer.right = (rPlayer.left + 8 * iUnit);
                rPlayer.top = fBottom + rMid.top - (((rMid.top - rMid.bottom) / 5) * 2);
                rPlayer.bottom = fBottom + (rPlayer.top - (8 * iUnit));
            }
            public void Reset() {
                iDownSinLevel = 0;
                scrl.Reset(SK.iPlayerLevel);
                scrl2.Reset(SK.iGunLevel);
                scrl3.Reset(SK.iDiffLevel);
                iLGunLevel = 0;
                iLIGLevel = iMaxUpgradeLevel;
                iLRadiusLevel = 0;
                iLPlayerLevel = 0;

                sWhiteCircle.setTexture(imgWhiteCircle);
                sWhiteCircle.setRegion(0, 0, (ScreenWidth/100) * 25, 40*iUnit);
                sWhiteCircle.setBounds(0, 0, (ScreenWidth/100) * 25, 40*iUnit);
                sWhiteCircle.setOriginCenter();
                sWhiteCircle.setPosition((15*iUnit), 20*iUnit);
                bPlayTouch = false;
                bBackTouch = false;
                bDrawInf = false;
                bSlidingDown = false;
                fBottom = 0;
            }

            public void Draw(SpriteBatch batch1) {
                UpdateRects();

                batch1.setColor(1, 1, 1,1-(float)(Math.sin(Math.toRadians(iDownSinLevel))));
                DrawToRect(rFullScreen, imgBlueBkg, batch1);
                batch1.setColor(1,1,1,1);

                if (bSlidingDown) {
                    fBottom = (float)-(Math.sin(Math.toRadians(iDownSinLevel)) * ScreenHeight);
                    iDownSinLevel += 2;
                    if (iDownSinLevel == 90) {
                        iGamemode = 4;
                        UpdateGame();
                        bSlidingDown = false;
                    }
                }
                   //Bars
                    MainScreen.DrawBars(batch1);
                    nfnt.setScale(0.055f*iUnit);
                    nfnt.setColor(1,1,1,1);
                    nfnt.draw(batch1,"Upgrades",10*iUnit,rTopBar.top - (2*iUnit));
                   //Main square Bkg's
                    DrawToRect(rDisplayLeft,imgGreyBox,batch1);
                    DrawToRect(rDisplayMid,imgGreyBox,batch1);
                    DrawToRect(rDisplayright,imgGreyBox,batch1);

                   //Upgrade Imgs 1
                    Rect rLeft = new Rect();
                    rLeft.CopySquare(rDisplayLeft,4*iUnit);

                nfnt.setScale(0.035f*iUnit);
                nfnt.getBounds("Character",1,9,tb);
                nfnt.draw(batch1,"Character", (rDisplayLeft.CenterX()) - (tb.width / 2), rDisplayLeft.top + tb.height + Math.round(3 * iUnit));
                nfnt.setScale(0.05f*iUnit);

                    switch (iLPlayerLevel) {
                        case 0:
                            DrawToRect(rLeft, pYou.imgCost0, batch1);
                            break;
                        case 1:
                            DrawToRect(rLeft, pYou.imgCost1, batch1);
                            break;
                        case 2:
                            DrawToRect(rLeft, pYou.imgCost2, batch1);
                            break;
                        case 3:
                            DrawToRect(rLeft, pYou.imgCost3, batch1);
                            break;
                        case 4:
                            DrawToRect(rLeft, pYou.imgCost4, batch1);
                            break;
                        case 5:
                            DrawToRect(rLeft, pYou.imgCost5, batch1);
                            break;
                        case 6:
                            DrawToRect(rLeft, pYou.imgCost6, batch1);
                            break;
                        case 7:
                            DrawToRect(rLeft, pYou.imgCost7, batch1);
                            break;
                    }
                   //Upgrade Imgs 2
                nfnt.setColor(1, 1, 1, 1);
                    nfnt.setScale(0.035f*iUnit);
                    nfnt.getBounds("Weapon", 0, 6, tb);
                    nfnt.draw(batch1, "Weapon", (rDisplayMid.CenterX()) - ((tb.width) / 2), rDisplayMid.top + tb.height + Math.round(3 * iUnit));
                    nfnt.setScale(0.05f*iUnit);

                    Rect rItem = new Rect();
                rItem.CopySquare(rDisplayMid,5*iUnit);
                    switch (ch[scrl.iSelected].iWeapDraw[(iLGunLevel<ch[scrl.iSelected].iWeapDraw.length)? iLGunLevel:0]) {
                        case 1:
                            DrawToRect(rItem, imgRand, batch1);
                            break;
                        case 2:
                            DrawToRect(rItem, imgPistol, batch1);
                            break;
                        case 3:
                            DrawToRect(rItem, imgShotgun, batch1);
                            break;
                        case 4:
                            DrawToRect(rItem, imgBeam, batch1);
                            break;
                        case 5:
                            DrawToRect(rItem,imgBazooka , batch1);
                            break;
                    }


                   //Upgrade Imgs 3

                    rRight.left = (ScreenX / 3) * 2;
                    rRight.right = ScreenX;
                    rRight.top = fBottom + (75 * iUnit);
                    rRight.bottom = fBottom + (20 * iUnit);
                    //canvas.drawCircle(rRight.left + rRight.width() / 2, 48 * iUnit, iLRadiusLevel * 1.5f * iUnit, pUniversal);

                    rT.left = (rRight.left + (rRight.right - rRight.left) / 2);
                    rT.right = (rRight.left + (rRight.right - rRight.left) / 2 + (10 * iUnit));
                    rT.top = fBottom + (65 * iUnit);
                    rT.bottom =fBottom + (55 * iUnit);
                    DrawToRect(rT, brBoxes.imgRands[4], batch1);

                    rT.left = (rRight.left + (rRight.right - rRight.left) / 4);
                    rT.right =(rRight.left + (rRight.right - rRight.left) / 4 + (5 * iUnit));
                    rT.top =fBottom +  (60 * iUnit);
                    rT.bottom = fBottom + (55 * iUnit);
                    DrawToRect(rT, brBoxes.imgRands[6], batch1);

                    rT.left = (rRight.left + (rRight.right - rRight.left) / 2.5f);
                    rT.right = (rRight.left + (rRight.right - rRight.left) / 2.5f + (5 * iUnit));
                    rT.top = fBottom + (55 * iUnit);
                    rT.bottom = fBottom + (50 * iUnit);
                    DrawToRect(rT, brBoxes.imgRands[5], batch1);

                    rT.left = (rRight.left + (rRight.right - rRight.left) / 5);
                    rT.right = (rRight.left + (rRight.right - rRight.left) / 5 + (15 * iUnit));
                    rT.top = fBottom + (50 * iUnit);
                    rT.bottom = fBottom + (35 * iUnit);
                    DrawToRect(rT, brBoxes.imgRands[7], batch1);

                    nfnt.setScale(0.035f*iUnit);
                    nfnt.getBounds("Difficulty", 0, "Difficulty".length(), tb);
                    nfnt.draw(batch1, "Difficulty", rDisplayright.CenterX() - ((tb.width) / 2), rDisplayright.top + tb.height + Math.round(3 * iUnit));
                    nfnt.setScale(0.03f*iUnit);


                   //Sliders
                    scrl.Draw(batch1);
                    scrl2.Draw(batch1);
                    scrl3.Draw(batch1);
                    if (iLPlayerLevel > SK.iMaxPlayerLevel) {
                        DrawToRect(rDisplayLockLeft, imgLocked, batch1);
                        Rect rUnlock = new Rect();
                        rUnlock.left = rDisplayLeft.right - (8*iUnit);
                        rUnlock.right = rDisplayLeft.right;
                        rUnlock.bottom = fBottom + rDisplayLeft.bottom;
                        rUnlock.top = fBottom + rDisplayLeft.bottom + (8*iUnit);
                        DrawToRect(rUnlock, imgUnlock, batch1);
                        nfnt.setColor(1,1,1,1);
                        nfnt.getBounds(SK.GetFormattedCash(iPlayPrice[iLPlayerLevel]), 0, SK.GetFormattedCash(iPlayPrice[iLPlayerLevel]).length(), tb);
                        nfnt.draw(batch1,SK.GetFormattedCash(iPlayPrice[iLPlayerLevel]), (rDisplayLeft.left), rDisplayLeft.bottom + tb.height);
                    } else {
                        SK.iPlayerLevel = iLPlayerLevel;
                        pYou.ChangeCostume(iLPlayerLevel);
                    }
                    if (iLGunLevel > ch[iLPlayerLevel].iWeapLev) {

                        DrawToRect(rDisplayLockMid, imgLocked, batch1);
                        Rect rUnlock = new Rect();
                        rUnlock.left = rDisplayMid.right - (8*iUnit);
                        rUnlock.right = rDisplayMid.right;
                        rUnlock.bottom = fBottom + rDisplayMid.bottom;
                        rUnlock.top = fBottom + rDisplayMid.bottom + (8*iUnit);
                        DrawToRect(rUnlock, imgUnlock, batch1);
                        nfnt.setColor(1,1,1,1);
                        nfnt.getBounds(SK.GetFormattedCash(ch[iLPlayerLevel].iWeapCost[scrl2.iSelected]), 0, SK.GetFormattedCash(ch[iLPlayerLevel].iWeapCost[scrl2.iSelected]).length(), tb);
                        nfnt.draw(batch1,SK.GetFormattedCash(ch[iLPlayerLevel].iWeapCost[scrl2.iSelected]), rDisplayMid.left, rDisplayMid.bottom + tb.height);
                    } else {
                        SK.iGunLevel = iLGunLevel;
                        ch[iLPlayerLevel].iWeapSelected = iLGunLevel;
                    }
                    if (iLRadiusLevel > SK.iMaxDiffLevel) {
                        DrawToRect(rDisplayLockRight, imgLocked, batch1);
                        Rect rUnlock = new Rect();
                        rUnlock.left = rDisplayright.right - (8*iUnit);
                        rUnlock.right = rDisplayright.right;
                        rUnlock.bottom = fBottom + rDisplayright.bottom;
                        rUnlock.top = fBottom + rDisplayright.bottom + (8*iUnit);
                        DrawToRect(rUnlock, imgUnlock, batch1);
                        nfnt.setColor(1,1,1,1);
                        nfnt.getBounds(SK.GetFormattedCash(iRadiusPrice[iLRadiusLevel]), 0, SK.GetFormattedCash(iRadiusPrice[iLRadiusLevel]).length(), tb);
                        nfnt.draw(batch1,SK.GetFormattedCash(iRadiusPrice[iLRadiusLevel]), rDisplayright.left, rDisplayright.bottom + tb.height);
                    } else {
                        SK.iDiffLevel = iLRadiusLevel;
                    }
                   //Buttons
                    DrawToRect(rPlay, bPlayTouch ? MainScreen.imgBtnPressed :MainScreen.imgButtonBKG, batch1);
                    DrawToRect(rBack, bBackTouch ? MainScreen.imgBtnPressed :MainScreen.imgButtonBKG, batch1);
                    nfnt.setColor(bPlayTouch ? 0 : 0.8f, bPlayTouch ? 0 : 1, 0.8f,1);
                    nfnt.setScale(0.04f*iUnit);
                    nfnt.getBounds("Start", 0, 5, tb);
                    nfnt.draw(batch1,"Start",rPlay.left + (((rPlay.right - rPlay.left) - (tb.width))/2), rPlay.top - (((rPlay.top - rPlay.bottom) - (tb.height))/2));
                    nfnt.getBounds("Back",0,4,tb);
                    nfnt.setColor(1, bBackTouch ? 0 : 1, bBackTouch ? 0 : 1, 1);
                    nfnt.draw(batch1,"Back",rBack.left + (((rBack.right - rBack.left) - (tb.width))/2), rBack.top - (((rBack.top - rBack.bottom) - (tb.height))/2));
                    nfnt.setColor(1, 1, 1, 1);

                   //Cash and infoBtn Display
                    nfnt.setScale(0.04f * iUnit);
                    nfnt.getBounds( SK.GetCash().toString(),tb);
                    rCashDisp.left = (rFullScreen.CenterX()) - ((8*iUnit + tb.width)/2);
                    rCashDisp.right = (rFullScreen.CenterX()) + ((8*iUnit + tb.width)/2);
                nfnt.setColor(1, 1, 1, 1);
                    SK.DrawCash(rCashDisp,batch1,0.04f*iUnit);
                nfnt.setScale(0.05f * iUnit);

                    if (bDrawInf) DrawToRect(rFullScreen,imgInfo,batch1);
                    DrawToRect(rInfButton,imgInfobtn,batch1);
                   //Other
                    iLGunLevel = scrl2.iSelected;
                    iLIGLevel = iMaxUpgradeLevel - iLGunLevel;
                    iLPlayerLevel = scrl.iSelected;
                    iLRadiusLevel = scrl3.iSelected;
            }

            public void touchDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = rBack;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    bBackTouch = true;
                    if (bVibrate) Gdx.input.vibrate(10);
                }
                TRect = rPlay;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    bPlayTouch = true;
                    if (bVibrate) Gdx.input.vibrate(10);
                }
                TRect = scrl.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl.Touch(fX, fY);
                }
                TRect = scrl2.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl2.Touch(fX, fY);
                }
                TRect = scrl3.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl3.Touch(fX, fY);
                }
                TRect = rInfButton;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    bDrawInf = true;
                }
                TRect = rDisplayLockLeft;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    if (ch[iLPlayerLevel].iCharCost < SK.GetCash()) {
                        SK.iMaxPlayerLevel = iLPlayerLevel;
                        SK.DeductCash(iPlayPrice[iLPlayerLevel]);
                        pYou.ShowSign("Minus " + SK.GetFormattedCash(iPlayPrice[iLPlayerLevel]), 20 * iUnit, 5 * iUnit, 300);
                        if (bVibrate) Gdx.input.vibrate(10);
                    } else {
                        if (bVibrate) Gdx.input.vibrate(50);
                    }
                }
                TRect = rDisplayLockMid;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    if (ch[iLPlayerLevel].iWeapCost[scrl2.iSelected] < SK.GetCash()) {
                        ch[iLPlayerLevel].iWeapLev = iLGunLevel;
                        ch[iLPlayerLevel].iWeapSelected = iLGunLevel;
                        ch[iLPlayerLevel].saveData();
                        SK.DeductCash(ch[iLPlayerLevel].iWeapCost[scrl2.iSelected]);
                        pYou.ShowSign("Minus " + SK.GetFormattedCash(ch[iLPlayerLevel].iWeapCost[scrl2.iSelected]), 20 * iUnit, 5 * iUnit, 180);
                    }
                }
                TRect = rDisplayLockRight;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    if (iRadiusPrice[iLRadiusLevel] < SK.GetCash()) {
                        SK.iMaxDiffLevel = iLRadiusLevel;
                        SK.DeductCash(iRadiusPrice[iLRadiusLevel]);
                        pYou.ShowSign("Minus " + SK.GetFormattedCash(iRadiusPrice[iLRadiusLevel]), 20 * iUnit, 5 * iUnit, 180);
                    }
                }
            }



            public void releaseDetector(float fX,float fY) {
                scrl.TouchLift();
                scrl2.TouchLift();
                scrl3.TouchLift();
                SK.Save();
                ch[iLPlayerLevel].saveData();
                Rect TRect = new Rect();
                TRect = scrl.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl2.Reset(0);
                }
                TRect = rBack;
                bPlayTouch = false;
                bBackTouch = false;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    iGamemode = 2;
                    UpdateGame();
                }
                TRect = rPlay;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    bSlidingDown = true;
                    pYou.Reset();
                }
                TRect = rInfButton;
                bDrawInf = false;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                }
            }

            public void MoveDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = scrl.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl.MoveTouch(fX, fY);
                }
                TRect = scrl2.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl2.MoveTouch(fX, fY);
                }
                TRect = scrl3.rOutput;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    scrl3.MoveTouch(fX, fY);
                }
            }

            public void Restore(Float fX, Float fY) {

            }

            class FingerScroller {
                private float
                        fLeftToRight,
                        fDisplayWidth,
                        fOutWidth,
                        fOutheight,
                        fUnitP,
                        fXref,
                        fXAcc,
                        fXVel,
                        fLefts[];
                private Rect
                        rDisplayBox = new Rect(),
                        rItems[],
                        rItemsTrack[],
                        rTextHeight;
                public Rect
                        rMainBox = new Rect();
                private Integer
                        iNumItems,
                        iVib1,
                        iVib2,
                        iSclNum;
                public Integer
                        iSelected = 0;
                private Boolean
                        bTouching = false,
                        bRand,bPistol,bShotgun,bBazooka,bBeam;

                public void Setup(Rect rOutputP, float fUnitPP, Integer iNump, Integer iStart,Integer iScrlNum) {
                    rMainBox = rOutputP;
                    fUnitP = fUnitPP;
                    iNumItems = iNump;
                    iSclNum = iScrlNum;

                    fOutWidth = rMainBox.right - rMainBox.left;
                    fOutheight = rMainBox.top - rMainBox.bottom;

                    fDisplayWidth = 10 * fUnitP;
                    rDisplayBox.left = rMainBox.left + ((fOutWidth / 2) - (fDisplayWidth / 2));
                    rDisplayBox.right = rMainBox.left + ((fOutWidth / 2) + (fDisplayWidth / 2));
                    rDisplayBox.top = rMainBox.bottom  +((fOutheight / 2) + (fDisplayWidth / 2));
                    rDisplayBox.bottom = rMainBox.bottom + ((fOutheight / 2) - (fDisplayWidth / 2));
                    nfnt.getBounds("LA", 0, 2, tb);
                    rTextHeight = new Rect();
                    rTextHeight.left = 0;
                    rTextHeight.bottom = 0;
                    rTextHeight.top = tb.height;
                    rTextHeight.right = tb.width;

                    float fDispDiff = rDisplayBox.top - rDisplayBox.bottom ;
                    float fTextDiff = rTextHeight.top - rTextHeight.bottom;
                    rItems = new Rect[iNumItems];
                    rItemsTrack = new Rect[iNumItems];
                    for (int i = 0; i < rItems.length; i++) {
                        rItems[i] = new Rect();
                        rItems[i].left = rDisplayBox.left + ((i * (fOutWidth * 3) / iNumItems));
                        rItems[i].right = (rItems[i].left + fDisplayWidth);
                        rItems[i].top = (rDisplayBox.top - ((fDispDiff - fTextDiff) / 2));
                        rItems[i].bottom = (rDisplayBox.bottom + ((fDispDiff - fTextDiff) / 2));
                        rItemsTrack[i] = new Rect();
                        rItemsTrack[i].left = rDisplayBox.left + ((i * (fOutWidth * 3) / iNumItems));
                        rItemsTrack[i].right = (rItemsTrack[i].left + fDisplayWidth);
                        rItemsTrack[i].top = (rDisplayBox.top - ((fDispDiff - fTextDiff) / 2));
                        rItemsTrack[i].bottom = (rDisplayBox.bottom + ((fDispDiff - fTextDiff) / 2));
                    }
                    fLefts = new float[rItems.length];
                    for (int i = 0; i < fLefts.length; i++) {
                        fLefts[i] = rItems[i].left - rDisplayBox.left;
                    }
                    fLeftToRight = rItems[rItems.length - 1].right - rItems[0].left;
                    fXAcc = 0.01f * fUnitP;
                    Reset(iStart);

                }

                public void Reset(Integer iStart) {
                    float fDispDiff = rDisplayBox.top - rDisplayBox.bottom;
                    float fTextDiff = rTextHeight.top - rTextHeight.bottom;
                    if (iSclNum == 2) {
                        iNumItems = ch[iLPlayerLevel].iNumItems;
                    }
                    rItems = new Rect[iNumItems];
                    for (int i = 0; i < rItems.length; i++) {
                        rItems[i] = new Rect();
                        rItems[i].left = (rDisplayBox.left - fLefts[iStart]) + (i * 12 * iUnit);
                        rItems[i].right = (rItems[i].left + fDisplayWidth);
                        rItems[i].top = (rDisplayBox.top - ((fDispDiff - fTextDiff) / 2));
                        rItems[i].bottom = (rDisplayBox.bottom + ((fDispDiff - fTextDiff) / 2));
                        rItemsTrack[i] = new Rect();
                        rItemsTrack[i].left = (rDisplayBox.left - fLefts[iStart]) + ((i * 12 * iUnit));
                        rItemsTrack[i].right = (rItemsTrack[i].left + fDisplayWidth);
                        rItemsTrack[i].top = (rDisplayBox.top - ((fDispDiff - fTextDiff) / 2));
                        rItemsTrack[i].bottom = (rDisplayBox.bottom + ((fDispDiff - fTextDiff) / 2));
                    }
                    fLeftToRight = rItems[rItems.length - 1].right - rItems[0].left;
                }

                public void Touch(float fX, float fY) {
                    fXref = fX;
                    for (int i = 0; i < rItems.length; i++) {
                        rItemsTrack[i].left = rItems[i].left;
                        rItemsTrack[i].right = rItems[i].right;
                    }
                    bTouching = true;
                    iTouchScrler = iSclNum;
                }

                public void MoveTouch(float fX, float fY) {
                    float fBef = rItems[0].left;
                    for (int i = 0; i < rItems.length; i++) {
                        rItems[i].left = (rItemsTrack[i].left + (fX - fXref));
                        rItems[i].right = (rItems[i].left + fDisplayWidth);
                    }
                    float fAft = rItems[0].left;
                    fXVel = fAft - fBef;
                }

                public void TouchLift() {
                    bTouching = false;
                    iTouchScrler = -1;

                }

                public void Draw(SpriteBatch batch1) {
                    if (!bTouching) {
                        for (Rect rItem : rItems) {
                            rItem.left += fXVel;
                            rItem.right = (rItem.left + fDisplayWidth);
                            if (fXVel < 0) {
                                fXVel += fXAcc;
                            } else if (fXVel > 0) {
                                fXVel -= fXAcc;
                            }
                            if (fXVel < fXAcc && fXVel > -fXAcc) fXVel = 0;


                        }
                    }
                    if (rItems[0].left > rDisplayBox.left) {
                        for (int i = 0; i < rItems.length; i++) {
                            rItems[i].left = rDisplayBox.left + ((i * 12 * iUnit));
                            rItems[i].right = (rItems[i].left + fDisplayWidth);
                            rItemsTrack[i].left = rItems[i].left;
                            rItemsTrack[i].right = rItems[i].right;
                            fXVel = 0;
                        }
                    }
                    if (rItems[rItems.length - 1].right < rDisplayBox.right) {
                        for (int i = 0; i < rItems.length; i++) {
                            rItems[i].left = ((rDisplayBox.right - fLeftToRight) + ((i * 12 * iUnit)));
                            rItems[i].right = (rItems[i].left + fDisplayWidth);
                            rItemsTrack[i].left = rItems[i].left;
                            rItemsTrack[i].right = rItems[i].right;
                            fXVel = 0;
                        }
                    }
                    iVib2 = iVib1;
                    for (int i = 0; i < rItems.length; i++) {
                        if (rItems[i].left > (rDisplayBox.left - (2 * iUnit)) && rItems[i].right < (rDisplayBox.right + (2 * iUnit))) {
                            iSelected = i;
                            if (iSclNum == 1)
                                scrl2.Reset(0);
                            iVib1 = 1;
                        }
                    }
                    if (!(rItems[iSelected].left > (rDisplayBox.left - (2 * iUnit)) && rItems[iSelected].right < (rDisplayBox.right + (2 * iUnit)))) {
                        iVib1 = 0;
                    }
                    if (!(iVib1 == iVib2)) {
                        if (bVibrate) Gdx.input.vibrate(15);
                    }
                    nfnt.setColor(0.7f, 0.7f, 0.7f, 1);
                    DrawToRect(rMainBox, imgsliderBack, batch1);
                    if (iSclNum == 2) iNumItems = ch[iLPlayerLevel].iNumItems;
                    for (Integer i = 0; i < iNumItems; i++) {
                        Rect rItem = rItems[i];

                         if (iSclNum == 3) {
                             nfnt.getBounds(i.toString(), 0, i.toString().length(), tb);
                            if (rItem.left > rMainBox.left && rItem.right < rMainBox.right) {
                                float fDiff = (rItem.right - rItem.left) - (tb.width);
                                float fYDiff = (rItem.top - rItem.bottom) - (tb.height);
                                nfnt.draw(batch1, i.toString(), rItem.left + (fDiff / 2), rItem.top - fYDiff / 2);
                            }
                        } else if (iSclNum == 1){
                             if (rItem.left > rMainBox.left && rItem.right < rMainBox.right) {
                                 switch (i) {
                                     case 0:
                                         DrawToRect(rItem, pYou.imgCost0, batch1);
                                         break;
                                     case 1:
                                         DrawToRect(rItem, pYou.imgCost1, batch1);
                                         break;
                                     case 2:
                                         DrawToRect(rItem, pYou.imgCost2, batch1);
                                         break;
                                     case 3:
                                         DrawToRect(rItem, pYou.imgCost3, batch1);
                                         break;
                                     case 4:
                                         DrawToRect(rItem, pYou.imgCost4, batch1);
                                         break;
                                     case 5:
                                         DrawToRect(rItem, pYou.imgCost5, batch1);
                                         break;
                                     case 6:
                                         DrawToRect(rItem, pYou.imgCost6, batch1);
                                         break;
                                     case 7:
                                         DrawToRect(rItem, pYou.imgCost7, batch1);
                                         break;
                                 }
                             }
                         } else {
                             if (iSclNum == 2) {
                                 switch (ch[iLPlayerLevel].iWeapDraw[i]) {
                                     case 1:
                                         DrawToRect(rItem, imgRand, batch1);
                                         break;
                                     case 2:
                                         DrawToRect(rItem, imgPistol, batch1);
                                         break;
                                     case 3:
                                         DrawToRect(rItem, imgShotgun, batch1);
                                         break;
                                     case 4:
                                         DrawToRect(rItem, imgBeam, batch1);
                                         break;
                                     case 5:
                                         DrawToRect(rItem,imgBazooka , batch1);
                                         break;
                                 }
                             }
                         }
                    }
                    DrawToRect(rMainBox, imgSliderFront, batch1);

                }

            }
            class FS {
                private Integer
                        iSelected,
                        iSelfNum,
                        iNumItems;
                private float
                        fLeft,
                        fRight,
                        fLeftTrack,
                        fWidth,
                        fMargin,
                        fDisplayWidth,
                        fTextDiff,
                        fDispDiff,
                        fXAcc,
                        fXVel,
                        fXref;
                private Rect
                        rOutput = new Rect(),
                        rOulineHArdCopy = new Rect(),
                        rCentre = new Rect(),
                        rTextHeight = new Rect(),
                        rBoxes[];
                private Boolean
                        bTouching,
                        bVib;

                public void Setup(Rect rOutLine,Integer iNumItemsP, Integer iStart,Integer iSelfNumP) {
                    iSelfNum = iSelfNumP;
                    rOulineHArdCopy.RectCopy(rOutLine);
                    rOutput.RectCopy(rOulineHArdCopy);
                    iNumItems = iNumItemsP;


                    nfnt.getBounds("LA", 0, 2, tb);
                    rTextHeight = new Rect();
                    rTextHeight.left = 0;
                    rTextHeight.bottom = 0;
                    rTextHeight.top = tb.height;
                    rTextHeight.right = tb.width;


                    fDisplayWidth = 7 * iUnit;

                    fXAcc = 0.2f;
                }
                private void UpdateRects() {
                    rOutput.RectCopy(rOulineHArdCopy);
                    rOutput.MoveDown(-fBottom);
                    float fOutWidth = rOutput.right - rOutput.left;
                    float fOutheight = rOutput.top - rOutput.bottom;
                    rCentre.left = rOutput.left + ((fOutWidth / 2) - (fDisplayWidth / 2));
                    rCentre.right = rOutput.left + ((fOutWidth / 2) + (fDisplayWidth / 2));
                    rCentre.top = rOutput.bottom  +((fOutheight / 2) + (fDisplayWidth / 2));
                    rCentre.bottom = rOutput.bottom + ((fOutheight / 2) - (fDisplayWidth / 2));
                }
                public void Reset(Integer iStart) {
                    rBoxes = new Rect[15];
                    fDispDiff = rCentre.top - rCentre.bottom;

                    fTextDiff = rTextHeight.top - rTextHeight.bottom;
                    fWidth = 7*iUnit;
                    fMargin = 2*iUnit;

                    fLeft = rCentre.left - ((iStart *fWidth) + ((iStart - 1) * fMargin));
                    fRight = fLeft +  (iNumItems * fWidth) + ((iNumItems - 1) * fMargin);

                    for (int i = 0; i < rBoxes.length; i++) {
                        rBoxes[i] = new Rect();
                        rBoxes[i].left =  fLeft + ((i * fWidth) + (i * fMargin));
                        rBoxes[i].right = (rBoxes[i].left + fDisplayWidth);
                        rBoxes[i].top = (rCentre.top - ((fDispDiff - fTextDiff) / 2));
                        rBoxes[i].bottom = (rCentre.bottom + ((fDispDiff - fTextDiff) / 2));
                    }
                    bVib = false;
                    bTouching = false;
                    iSelected = iStart;
                }
                private void AlignZero() {
                    fLeft = rCentre.left;
                    fRight = fLeft +  (iNumItems * fWidth) + ((iNumItems - 1) * fMargin);
                }
                public void Draw(SpriteBatch batch1) {
                    UpdateRects();
                    if (iSelfNum == 2) iNumItems = ch[scrl.iSelected].iNumItems;
                    fRight = fLeft +  (iNumItems * fWidth) + ((iNumItems - 1) * fMargin);
                    for (int i = 0; i < iNumItems; i++) {
                        rBoxes[i] = new Rect();
                        rBoxes[i].left = fLeft + ((i * fWidth) + (i * fMargin));
                        rBoxes[i].right = (rBoxes[i].left + fDisplayWidth);
                        rBoxes[i].top = fBottom + (rCentre.top - ((fDispDiff - fTextDiff) / 2));
                        rBoxes[i].bottom = fBottom + (rCentre.bottom + ((fDispDiff - fTextDiff) / 2));
                    }

                    if (!bTouching) {
                            fLeft += fXVel;
                            if (fXVel < 0) {
                                fXVel += fXAcc;
                            } else if (fXVel > 0) {
                                fXVel -= fXAcc;
                            }
                            if (fXVel < fXAcc && fXVel > -fXAcc) fXVel = 0;
                    }
                    if (fLeft > rCentre.left && !bTouching) {
                            fLeft = rCentre.left;
                            fXVel = 0;
                    }
                    if (fRight < rCentre.right && !bTouching) {
                            fLeft = rCentre.right -(fRight - fLeft);
                            fXVel = 0;
                    }
                    for (int i = 0; i < iNumItems; i++) {
                        if (rBoxes[i].CenterX() > (rCentre.left + (2 * iUnit)) && rBoxes[i].CenterX() < (rCentre.right - (2 * iUnit))) {
                            iSelected = i;

                            if (bVib) {
                                if (bVibrate) Gdx.input.vibrate(15);
                                bVib = false;
                            }
                        }
                    }
                    if ((rBoxes[iSelected].CenterX() < (rCentre.left + (2 * iUnit)) || rBoxes[iSelected].CenterX() > (rCentre.right - (2 * iUnit)))) {
                        if (!bVib) {
                            //if (bVibrate) Gdx.input.vibrate(15);
                            bVib = true;
                        }
                    }
                    nfnt.setColor(0.7f, 0.7f, 0.7f, 1);
                    DrawToRect(rOutput, imgsliderBack, batch1);
                    for (Integer i = 0; i < iNumItems; i++) {
                        Rect rItem = rBoxes[i];
                        if (iSelfNum == 3) {
                            nfnt.getBounds(i.toString(), 0, i.toString().length(), tb);
                            if (rItem.left > rOutput.left && rItem.right < rOutput.right) {
                                float fDiff = (rItem.right - rItem.left) - (tb.width);
                                float fYDiff = (rItem.top - rItem.bottom) - (tb.height);
                                nfnt.draw(batch1, i.toString(), rItem.left + (fDiff / 2), rItem.top - fYDiff / 2);
                            }
                        } else if (iSelfNum == 1) {
                            if (rItem.left > rOutput.left && rItem.right < rOutput.right) {
                                switch (i) {
                                    case 0:
                                        DrawToRect(rItem, pYou.imgCost0, batch1);
                                        break;
                                    case 1:
                                        DrawToRect(rItem, pYou.imgCost1, batch1);
                                        break;
                                    case 2:
                                        DrawToRect(rItem, pYou.imgCost2, batch1);
                                        break;
                                    case 3:
                                        DrawToRect(rItem, pYou.imgCost3, batch1);
                                        break;
                                    case 4:
                                        DrawToRect(rItem, pYou.imgCost4, batch1);
                                        break;
                                    case 5:
                                        DrawToRect(rItem, pYou.imgCost5, batch1);
                                        break;
                                    case 6:
                                        DrawToRect(rItem, pYou.imgCost6, batch1);
                                        break;
                                    case 7:
                                        DrawToRect(rItem, pYou.imgCost7, batch1);
                                        break;
                                }
                            }
                        } else if (iSelfNum == 2) {
                            if (rItem.left > rOutput.left && rItem.right < rOutput.right) {

                                switch (ch[scrl.iSelected].iWeapDraw[i]) {
                                    case 1:
                                        DrawToRect(rItem, imgRand, batch1);
                                        break;
                                    case 2:
                                        DrawToRect(rItem, imgPistol, batch1);
                                        break;
                                    case 3:
                                        DrawToRect(rItem, imgShotgun, batch1);
                                        break;
                                    case 4:
                                        DrawToRect(rItem, imgBeam, batch1);
                                        break;
                                    case 5:
                                        DrawToRect(rItem,imgBazooka , batch1);
                                        break;
                                }
                            }
                        }

                    }
                    DrawToRect(rOutput, imgSliderFront, batch1);

                }
                public void Touch(float fX, float fY) {
                    fXref = fX;
                    fLeftTrack = fLeft;
                    bTouching = true;
                    iTouchScrler = iSelfNum;
                    if (iSelfNum ==1) {
                        scrl2.Reset(0);
                    }
                }

                public void MoveTouch(float fX, float fY) {
                    float fBef = fLeft;
                        fLeft = (fLeftTrack + (fX - fXref));
                    float fAft = fLeft;
                    fXVel = fAft - fBef;
                }

                public void TouchLift() {
                    bTouching = false;
                    iTouchScrler = -1;

                }
            }
            class character {
                public Integer
                        iChar,
                        iWeapLev,
                        iWeapSelected,
                        iWeapDraw[],
                        iWeapCost[] = new Integer[]{0,200,1000,2000,5000,10000},
                        iCharCost,
                        iNumItems;

                public boolean
                        bRand,
                        bPistol,
                        bShotgun,
                        bBazooka,
                        bBeam;
                public void saveData() {
                    SK.SaveValue("CharWeapLevel" + iChar,iWeapLev);
                    SK.SaveValue("CharWeapSelLevel" + iChar,iWeapSelected);
                }
                public void getData() {
                    iWeapLev = SK.GetValue("CharWeapLevel" + iChar);
                    iWeapSelected = SK.GetValue("CharWeapSelLevel" + iChar);
                }
                public void Setup(Integer iCharP) {
                    iChar = iCharP;
                    getData();
                    iNumItems = 0;
                    switch (iCharP) {
                        case 0:
                            bRand = true;
                            bPistol = true;
                            bShotgun = false;
                            bBazooka = false;
                            bBeam = false;
                            iCharCost = 0;
                            iWeapDraw = new Integer[]{iR,iP};
                            break;
                        case 1:
                            bRand = true;
                            bPistol = true;
                            bShotgun = true;
                            bBazooka = false;
                            bBeam = false;
                            iCharCost = 100;
                            iWeapDraw = new Integer[]{iR,iP,iS};
                            break;
                        case 2:
                            bRand = true;
                            bPistol = false;
                            bShotgun = true;
                            bBazooka = false;
                            bBeam = false;
                            iCharCost = 500;
                            iWeapDraw = new Integer[]{iR,iS};
                            break;
                        case 3:
                            bRand = false;
                            bPistol = true;
                            bShotgun = true;
                            bBazooka = false;
                            bBeam = false;
                            iCharCost = 1000;
                            iWeapDraw = new Integer[]{iP,iS};
                            break;
                        case 4:
                            bRand = false;
                            bPistol = true;
                            bShotgun = false;
                            bBazooka = true;
                            bBeam = false;
                            iCharCost = 2000;
                            iWeapDraw = new Integer[]{iP,iBa};
                            break;
                        case 5:
                            bRand = false;
                            bPistol = true;
                            bShotgun = true;
                            bBazooka = true;
                            bBeam = false;
                            iCharCost = 3000;
                            iWeapDraw = new Integer[]{iP,iS,iBa};
                            break;
                        case 6:
                            bRand = false;
                            bPistol = true;
                            bShotgun = false;
                            bBazooka = false;
                            bBeam = false;
                            iCharCost = 5000;
                            iWeapDraw = new Integer[]{iP};
                            break;
                        case 7:
                            bRand = false;
                            bPistol = false;
                            bShotgun = false;
                            bBazooka = false;
                            bBeam = true;
                            iCharCost = 8000;
                            iWeapDraw = new Integer[]{iBe};
                            break;
                    }
                    if (bRand) {
                        iNumItems++;
                    }
                    if (bPistol) {
                        iNumItems++;
                    }
                    if (bShotgun) {
                        iNumItems++;
                    }
                    if (bBazooka) {
                        iNumItems++;
                    }
                    if (bBeam) {
                        iNumItems++;
                    }

                }
            }
        }///////////////////////
        //////////////////////////////
        class PauseMenu {
            private Integer
                    iAnim,
                    iContAnim;
            private float
                    fdecel;
            private Rect
                    rMenuArrow = new Rect(),
                    rReset = new Rect(),
                    rTextBit = new Rect();
            private Texture
                    imgRedBack;
            private Boolean
                    bBackPressed,
                    bOnce;

            public void Destroy() {
                imgRedBack.dispose();
            }
            public void touchDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = rMenuArrow;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    if (bVibrate) Gdx.input.vibrate(10);
                    bBackPressed = true;
                }
            }
            public void ReleaseDetector(Float fX, Float fY) {
                Rect TRect = new Rect();
                TRect = rMenuArrow;
                Rect TRect2 = new Rect();
                TRect2 = SDD.rSettingsbtn;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    iGamemode = 5;
                    UpdateGame();
                } else if ((fX > TRect2.left && fX < TRect2.right) && (fY < TRect2.top && fY > TRect2.bottom)) {
                } else {
                    iGamemode = 4;
                    UpdateGame();
                }
                bBackPressed = false;
            }
            public void Reset() {
                iAnim = 0;
                rMenuArrow.left = ScreenWidth/2 - (17*iUnit);
                rMenuArrow.right = ScreenWidth/2 + (17*iUnit);
                rMenuArrow.top = (20 * iUnit);
                rMenuArrow.bottom = rMenuArrow.top - (15 * iUnit);
                rReset.left = ScreenX - (20 * iUnit);
                rReset.right = ScreenX;
                rReset.top = (40 * iUnit);
                rReset.bottom = (60 * iUnit);
                rTextBit.left = 0;
                rTextBit.right = rTextBit.left + (100 * iUnit);
                rTextBit.top = ScreenY - (50 * iUnit);
                rTextBit.bottom = ScreenY;
                iContAnim = 0;
                fdecel = 20;
                bBackPressed = false;
                bOnce = true;
            }
            public void Setup() {
                iAnim = 0;
                rMenuArrow.left = ScreenWidth/2 - (15*iUnit);
                rMenuArrow.right = ScreenWidth/2 + (15*iUnit);
                rMenuArrow.top = (13.5f * iUnit);
                rMenuArrow.bottom = (1.5f * iUnit);
                rReset.left = ScreenX - (20 * iUnit);
                rReset.right = ScreenX;
                rReset.top = (40 * iUnit);
                rReset.bottom = (60 * iUnit);
                rTextBit.left = 0;
                rTextBit.right = rTextBit.left + (100 * iUnit);
                rTextBit.top = ScreenY - (50 * iUnit);
                rTextBit.bottom = ScreenY;
                iContAnim = 0;
                fdecel = 20;
                imgRedBack = new Texture("redback.jpg");
                bBackPressed = false;
            }

            public void DrawPause(SpriteBatch batch1) {
                batch1.setColor(1, 0, 0, iAnim * (0.6f / 200));
                DrawToRect(rFullScreen, imgRedBack, batch1);
                batch1.setColor(1, 1, 1, iAnim * (1f / 200));
                DrawToRect(rMenuArrow,bBackPressed? MainScreen.imgBtnPressed : MainScreen.imgButtonBKG,batch1);
                nfnt.setColor(1, bBackPressed ? 0 : 1, bBackPressed ? 0 : 1, iAnim * (0.9f / 200));
                nfnt.setScale(0.05f * iUnit);
                nfnt.getBounds("Back",0,4,tb);
                nfnt.draw(batch1,"Back",rMenuArrow.left + (((rMenuArrow.right - rMenuArrow.left) - (tb.width))/2), rMenuArrow.top - (((rMenuArrow.top - rMenuArrow.bottom) - (tb.height))/2));

                if (iAnim < 200) {
                    iAnim = iAnim + 4;
                    iContAnim = iContAnim + Math.round(fdecel);
                    fdecel = fdecel - 0.05f;
                } else if (iAnim >= 200 && iContAnim < 1200) {
                    iContAnim = iContAnim + Math.round(fdecel);
                    fdecel = fdecel - 0.05f;
                } else if (iContAnim >= 1200 && bOnce) {
                    pYou.ShowSign("Plus $" + SK.GetScore().toString(), Math.round(13 * iUnit), 5 * iUnit, 230);
                    bOnce = false;
                }
                nfnt.setColor(1, 1, 1, iAnim * (1f / 200));
                nfnt.setScale(0.05f * iUnit);
                nfnt.getBounds(SK.GetCashf(),0,SK.GetCashf().length(),tb);

                Rect rCashDisp = new Rect();
                nfnt.setScale(0.055f * iUnit);
                nfnt.getBounds( SK.GetCash().toString(),tb);
                rCashDisp.bottom = 4*iUnit;
                rCashDisp.top = rCashDisp.bottom + tb.height;
                rCashDisp.left = ((ScreenX/6)) - ((8*iUnit + tb.width)/2);
                rCashDisp.right = ((ScreenX/6)) + ((8*iUnit + tb.width)/2);
                SK.DrawCash(rCashDisp,batch1,0.055f*iUnit);
                nfnt.setScale(0.05f * iUnit);

                nfnt.setColor(0.8f, 0.8f, 0.8f, iAnim * (1f / 200));
                nfnt.setScale(0.05f * iUnit);
                nfnt.draw(batch1, "        Score           " + SK.GetScore().toString(), Math.round(100 * iUnit) - Math.round((iContAnim * iUnit) / 20.1), Math.round(60 * iUnit));
                nfnt.draw(batch1, "       Highcore    " + SK.GetHighScore().toString(), Math.round(-40 * iUnit) + Math.round((iContAnim * iUnit) / 15), Math.round(50 * iUnit));
                nfnt.setScale(0.04f*iUnit);
                nfnt.draw(batch1, "        Distance          " + DC.GetDistS(), Math.round(100 * iUnit) - Math.round((iContAnim * iUnit) / 20.1), Math.round(40 * iUnit));
                nfnt.draw(batch1, "        Best                 " + DC.GetHighDistS(), Math.round(-40 * iUnit) + Math.round((iContAnim * iUnit) / 15), Math.round(32 * iUnit));

                fnt.setColor(0, 0, 0, iAnim * (0.9f / 200));
                fnt.setScale(0.12f*iUnit);
                fnt.getBounds("You lose",0,8,tb);
                fnt.draw(batch1, "You lose", Math.round(100 * iUnit) - Math.round((iContAnim * iUnit) / 20.1), (85*iUnit));
                fnt.setColor(1, 1, 1, iAnim * (0.9f / 200));
                fnt.draw(batch1, "You lose", Math.round(-20 * iUnit) + Math.round((iContAnim * iUnit) / 20), (85*iUnit));

            }

        }//////////////////////////
        //////////////////////////////
        class SettingDropDown {
            private Integer
                    iAngle,
                    iAngTarg,
                    iAnim;
            private Texture
                    imgDraw;
            public Boolean
                    bSettingsUp,bInMotion;
            private Rect
                    rSettings = new Rect(),
                    rSound = new Rect(),
                    rVib = new Rect(),
                    rFlip = new Rect(),
                    rSettingsbtn = new Rect(),
                    rSetBox = new Rect();
            private Sprite
                    sSettingsDef,
                    sSettingsOth,
                    sSettingsSound,
                    sSettingsVib,
                    sSettingBack,
                    sSetCog;

            public void Destroy() {
                imgDraw.dispose();
            }
            public void Setup() {
                rSettingsbtn.left = ScreenX - (20 * iUnit);
                rSettingsbtn.right = ScreenX - (10 * iUnit);
                rSettingsbtn.top = ScreenY - (2.5f * iUnit);
                rSettingsbtn.bottom = ScreenY - (12.5f * iUnit);

                rVib.left = ScreenX - (76.9f * iUnit);
                rVib.right = ScreenX - (12.3f * iUnit);
                rVib.top = ScreenY - (30.7f * iUnit);
                rVib.bottom = rVib.top - (10 * iUnit);

                rSound.left = ScreenX - (76.9f * iUnit);
                rSound.right = ScreenX - (12.3f * iUnit);
                rSound.top = rVib.bottom;
                rSound.bottom = rSound.top - (10 * iUnit);

                rFlip.left = ScreenX - (76.9f * iUnit);
                rFlip.right = ScreenX - (12.3f * iUnit);
                rFlip.top = rSound.bottom;
                rFlip.bottom = rFlip.top - (15.5f * iUnit);

                bSettingsUp = true;
                rSettings.left = ScreenX / 2;
                rSettings.top = (rSettingsbtn.top);
                rSettings.right = (rSettingsbtn.right + (iUnit));
                rSettings.bottom = rSettings.top - (70 * iUnit);
                iAnim = 300;
                imgDraw = new Texture("UI/settingsdown.png");
                sSettingBack = new Sprite();
                sSettingBack.setTexture(imgDraw);
                sSettingBack.setRegion(imgDraw);
                sSettingBack.setBounds(0, 0, rSettings.right-rSettings.left, rSettings.top - rSettings.bottom);
                sSettingBack.setOrigin((rSettingsbtn.left + ((rSettingsbtn.right - rSettingsbtn.left)/2)-rSettings.left), rSettingsbtn.bottom + ((rSettingsbtn.top - rSettingsbtn.bottom)/2) - rSettings.bottom);
                sSettingBack.setPosition(rSettings.left,rSettings.bottom);

                sSettingsDef = new Sprite();
                sSettingsDef.setTexture(new Texture("UI/SettingScreendefault.png"));
                sSettingsDef.setRegion(new Texture("UI/SettingScreendefault.png"));
                sSettingsDef.setBounds(0, 0, rSettings.right-rSettings.left, rSettings.top - rSettings.bottom);
                sSettingsDef.setOrigin((rSettingsbtn.left + ((rSettingsbtn.right - rSettingsbtn.left)/2)-rSettings.left), rSettingsbtn.bottom + ((rSettingsbtn.top - rSettingsbtn.bottom)/2) - rSettings.bottom);
                sSettingsDef.setPosition(rSettings.left,rSettings.bottom);

                sSetCog = new Sprite();
                sSetCog.setTexture(imgCoginner);
                sSetCog.setRegion(imgCoginner);
                sSetCog.setBounds(0, 0, rSettingsbtn.right - rSettingsbtn.left,rSettingsbtn.top - rSettingsbtn.bottom);
                sSetCog.setOrigin((rSettingsbtn.right - rSettingsbtn.left)/2,(rSettingsbtn.top - rSettingsbtn.bottom)/2);
                sSetCog.setPosition(rSettingsbtn.left,rSettingsbtn.bottom);

                sSettingsOth = new Sprite();
                sSettingsOth.setTexture(new Texture("UI/SettingScreenOther.png"));
                sSettingsOth.setRegion(new Texture("UI/SettingScreenOther.png"));
                sSettingsOth.setBounds(0, 0, rSettings.right-rSettings.left, rSettings.top - rSettings.bottom);
                sSettingsOth.setOrigin((rSettingsbtn.left + ((rSettingsbtn.right - rSettingsbtn.left)/2)-rSettings.left), rSettingsbtn.bottom + ((rSettingsbtn.top - rSettingsbtn.bottom)/2) - rSettings.bottom);
                sSettingsOth.setPosition(rSettings.left,rSettings.bottom);

                sSettingsSound = new Sprite();
                sSettingsSound.setTexture(new Texture("UI/setsoundselected.png"));
                sSettingsSound.setRegion(new Texture("UI/setsoundselected.png"));
                sSettingsSound.setBounds(0, 0, rSettings.right-rSettings.left, rSettings.top - rSettings.bottom);
                sSettingsSound.setOrigin((rSettingsbtn.left + ((rSettingsbtn.right - rSettingsbtn.left)/2)-rSettings.left), rSettingsbtn.bottom + ((rSettingsbtn.top - rSettingsbtn.bottom)/2) - rSettings.bottom);
                sSettingsSound.setPosition(rSettings.left,rSettings.bottom);

                sSettingsVib = new Sprite();
                sSettingsVib.setTexture(new Texture("UI/setvibselected.png"));
                sSettingsVib.setRegion(new Texture("UI/setvibselected.png"));
                sSettingsVib.setBounds(0, 0, rSettings.right-rSettings.left, rSettings.top - rSettings.bottom);
                sSettingsVib.setOrigin((rSettingsbtn.left + ((rSettingsbtn.right - rSettingsbtn.left)/2)-rSettings.left), rSettingsbtn.bottom + ((rSettingsbtn.top - rSettingsbtn.bottom)/2) - rSettings.bottom);
                sSettingsVib.setPosition(rSettings.left,rSettings.bottom);
                iAngTarg = -90;
                iAngle = -90;
                bInMotion = false;
            }
            public void SettingsDown() {
                iAnim = 0;
                iAngTarg = 0;
                bPaused = true;
                bInMotion = true;
            }

            public void SettingsUp() {
                iAnim = 0;
                iAngTarg = -90;
                bInMotion = true;
            }

            public void Draw(SpriteBatch batch1) {
                if (iAngle < iAngTarg) {
                    iAngle += 2;
                } else if (iAngle > iAngTarg) {
                    iAngle -= 2;
                }
                if (iAngle == -90) {
                    bSettingsUp = true;
                    bPaused = false;
                    bInMotion = false;
                } else if (iAngle == 0) {
                    bSettingsUp = false;
                    bInMotion = false;
                }
                if (bInMotion || !bSettingsUp) {
                    sSettingBack.setRotation(0);
                    sSettingBack.rotate(iAngle);

                    sSettingsDef.setRotation(0);
                    sSettingsDef.rotate(iAngle);

                    sSettingsOth.setRotation(0);
                    sSettingsOth.rotate(iAngle);

                    sSettingsSound.setRotation(0);
                    sSettingsSound.rotate(iAngle);

                    sSettingsVib.setRotation(0);
                    sSettingsVib.rotate(iAngle);

                    sSetCog.setRotation(0);
                    sSetCog.rotate(iAngle);
                    batch1.setColor(0, 0, 0, (90 - -iAngle) * (0.5f / 90));
                    DrawToRect(rFullScreen, imgBlack, batch1);
                    if (iAngle < -30) {
                        sSettingBack.setAlpha(255 - ((60 - (-iAngle - 30)) * (255 / 60)));
                        sSettingsDef.setAlpha(255 - ((60 - (-iAngle - 30)) * (255 / 60)));
                        sSettingsOth.setAlpha(255 - ((60 - (-iAngle - 30)) * (255 / 60)));
                        sSettingsSound.setAlpha(255 - ((60 - (-iAngle - 30)) * (255 / 60)));
                        sSettingsVib.setAlpha(255 - ((60 - (-iAngle - 30)) * (255 / 60)));
                    } else {
                        sSettingBack.setAlpha(1);
                        sSettingsDef.setAlpha(1);
                        sSettingsOth.setAlpha(1);
                        sSettingsSound.setAlpha(1);
                        sSettingsVib.setAlpha(1);

                    }
                    sSettingBack.draw(batch1);
                    if (bFlipped) sSettingsDef.draw(batch1);
                    else sSettingsOth.draw(batch1);
                    if (bVibrate) sSettingsVib.draw(batch1);
                    if (bSound) sSettingsSound.draw(batch1);
                }
                batch1.setColor(1,1,1,1);
                DrawToRect(rSettingsbtn, imgCogBkg, batch1);
                sSetCog.draw(batch1);
            }

            public void TouchHandler(float fX, float fY) {
                Rect TRect = new Rect();
                TRect = rSettingsbtn;
                if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                    if (SDD.bSettingsUp) {
                        SDD.SettingsDown();
                    } else {
                        SDD.SettingsUp();
                    }
                }
                if (!bSettingsUp) {
                    TRect = rVib;
                    if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                        bVibrate = !bVibrate;
                    }
                    TRect = rSound;
                    if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                        bSound = !bSound;
                    }
                    TRect = rFlip;
                    if ((fX > TRect.left && fX < TRect.right) && (fY < TRect.top && fY > TRect.bottom)) {
                        bFlipped = !bFlipped;
                    }
                    SK.SaveValue("bVibrate",bVibrate);
                    SK.SaveValue("bSound",bSound);
                    SK.SaveValue("bFlipped",bFlipped);
                }
            }
        }////////////////////
        //////////////////////////////
        class Weapon {
            public Integer
                    iUpLevel,
                    iDownLevel,
                    iOuterLimit = 50,
                    iMaxAngle = 160,
                    iDiffLevel,
                    iBulType,
                    ibulangles[] = new Integer[]{45,22,0,-22,-45};
            public Texture
                    imgBullet,
                    imgBubble,
                    imgMissile,
                    imgShuriken;
            private Bullet
                    bMag[],
                    bMagR[];

            public void Reset(Integer iBulTypeP) {
                iBulType = iBulTypeP;
                bMag = new Bullet[1];
                bMag[0] = new Bullet();
                bMag[0].Setup(ScreenX, 50 * iUnit);
                iDiffLevel = SK.iGunLevel;
            }
            public void Destroy() {
                imgBullet.dispose();
                imgBubble.dispose();
                imgMissile.dispose();
                imgShuriken.dispose();
            }
            public void Setup() {
                imgBubble = new Texture("Weaponry/bulpurplebubble.png");
                imgBullet = new Texture("Weaponry/bulmissile.png");
                imgMissile = new Texture("Weaponry/Missile.png");
                imgShuriken = new Texture("Weaponry/Shuriken.png");
                bMag = new Bullet[1];
                bMag[0] = new Bullet();
                bMag[0].Setup(ScreenX, 50 * iUnit);
                iDiffLevel = SK.iDiffLevel;
            }

            public void Shoot(float fXP, float fYP) {

                 /*
                iR = 1,
                iP = 2,
                iS = 3,
                iBe = 4,
                iBa = 5;
                */ //3,5,15,20,15
                switch (iBulType) {
                    case 1:
                        SK.DecScore(3);
                        bMagR = bMag; //Shoot Bullet
                        bMag = new Bullet[bMagR.length + 1];
                        System.arraycopy(bMagR, 0, bMag, 0, bMagR.length);
                        bMag[bMag.length - 1] = new Bullet();
                        bMag[bMag.length - 1].Setup(fXP, fYP);
                        break;
                    case 2:
                        SK.DecScore(5);
                        bMagR = bMag; //Shoot Bullet
                        bMag = new Bullet[bMagR.length + 1];
                        System.arraycopy(bMagR, 0, bMag, 0, bMagR.length);
                        bMag[bMag.length - 1] = new Bullet();
                        bMag[bMag.length - 1].Setup(fXP, fYP);
                        break;
                    case 3:
                        SK.DecScore(15);
                        bMagR = bMag; //Shoot Bullet
                        bMag = new Bullet[bMagR.length + 5];
                        System.arraycopy(bMagR, 0, bMag, 0, bMagR.length);
                        for (int i = 1; i < 6; i++) {
                            bMag[bMag.length - i] = new Bullet();
                            bMag[bMag.length - i].Setup(fXP, fYP);
                        }
                        break;
                    case 4:
                        SK.DecScore(20);
                        pYou.ShootBeam();
                        break;
                    case 5:
                        SK.DecScore(15);
                        bMagR = bMag; //Shoot Bullet
                        bMag = new Bullet[bMagR.length + 1];
                        System.arraycopy(bMagR, 0, bMag, 0, bMagR.length);
                        bMag[bMag.length - 1] = new Bullet();
                        bMag[bMag.length - 1].Setup(fXP, fYP);
                        break;
                }
            }

            public void DeprecateBullet(Integer iWhich) {
                bMagR = bMag;
                bMag = new Bullet[bMag.length - 1];
                for (int j = 0; j < bMag.length; j++) {
                    if (j < iWhich) {
                        bMag[j] = bMagR[j];
                    } else {
                        bMag[j] = bMagR[j + 1];
                    }
                }
            }

            public void MoveBullets(float fChangeP) {
                if (!bPaused) {
                    if (iBulType != 3) {
                        for (int j = 0; j < bMag.length; j++) {
                            bMag[j].Update(fChangeP, 0);
                            if (bMag[j].rBullet.left > ScreenX) DeprecateBullet(j);
                        }
                    } else {
                        Integer iCount = 0;
                        for (int j = 0; j < bMag.length; j++ ) {
                            bMag[j].Update(fChangeP, ibulangles[iCount]);
                            if (bMag[j].rBullet.left > ScreenX) DeprecateBullet(j);
                            iCount++;
                            if (iCount == 5) iCount = 0;
                        }
                    }
                }
            }

            public void Draw(SpriteBatch batch1) {
                for (Bullet aBMag : bMag) aBMag.Draw(batch1);
            }

            class Bullet {
                public Rect
                        rBullet = new Rect(),
                        rArea = new Rect();
                private float
                        fXVel,
                        fYVel,
                        fAccel,
                        fATarget,
                        fAngle,
                        fAngTarget,
                        fIncBy = 0.1f;

                public void UpdateTargets() {
                    float fTopDist = rArea.top - rBullet.top;
                    float fBottomDist =rBullet.bottom - rArea.bottom;
                    fATarget = (rand.nextInt(9) + 1) * 0.3f;
                    if (!(fTopDist < ((rArea.top - rArea.bottom) / 3) && fBottomDist < ((rArea.top - rArea.bottom) / 3))) {
                        fAngTarget = rand.nextInt(Math.round(iDownLevel * 5 *  (270 / (5* iMaxUpgradeLevel)))) - ((iDownLevel *5* (270 / (5* iMaxUpgradeLevel))) / 2);
                    }
                }
                public void Setup(float fX, float fY) {
                    iUpLevel = SK.GetValue("GunLevel");
                    iDownLevel = iMaxUpgradeLevel - iUpLevel;
                    rArea.top = (fY + iUnit * ((iMaxUpgradeLevel - iUpLevel) * (iOuterLimit / iMaxUpgradeLevel)) / 2);
                    rArea.bottom = (fY - iUnit * ((iMaxUpgradeLevel - iUpLevel) * (iOuterLimit / iMaxUpgradeLevel)) / 2);
                    rArea.left = pYou.PlayerRect.left;
                    rArea.right = ScreenX;
                    rBullet.left = (fX - (2 * iUnit));
                    rBullet.right = (fX + (2 * iUnit));
                    rBullet.top = (fY + (2 * iUnit));
                    rBullet.bottom = (fY - (2 * iUnit));

                    fAngle = 0;
                    fATarget = 10;
                    fAccel = 0;
                    fXVel = 1;
                    fYVel = 1;
                    fAngTarget = 0;
                }
                /*
                iR = 1,
                iP = 2,
                iS = 3,
                iBe = 4,
                iBa = 5;
                */
                public void Update(float fChange,Integer iAngle) {
                    rBullet.left += (fXVel);
                    rBullet.right += (fXVel);
                    rBullet.top += (fYVel);
                    rBullet.bottom += (fYVel);

                    switch (iBulType) {
                        case 1:
                            if (fAccel < fATarget - 1) {
                            fAccel += fIncBy;
                            }
                            if (fAccel > fATarget + 1) {
                                fAccel -= fIncBy;
                            }
                            if (fAngle < fAngTarget - 4) {
                                fAngle += 9;
                            }
                            if (fAngle > fAngTarget + 4) {
                                fAngle -= 9;
                            }
                            if (fAngle < fAngTarget + 5 && fAngle > fAngTarget - 5) {
                                UpdateTargets();
                            }
                            if (fAccel > fATarget - 1 && fAccel < fATarget + 1) {
                                UpdateTargets();
                            }
                            fXVel = (float) Math.cos(Math.toRadians(fAngle)) * fAccel * iUnit;
                            fYVel = (float) Math.sin(Math.toRadians(fAngle)) * fAccel * iUnit;
                            break;
                        case 2:
                            if (fAccel < fATarget - 1) {
                                fAccel += fIncBy;
                            }
                            if (fAccel > fATarget + 1) {
                                fAccel -= fIncBy;
                            }
                            fXVel = (float) Math.cos(Math.toRadians(fAngle)) * fAccel * iUnit;
                            break;
                        case 3:
                            if (fAccel < fATarget - 1) {
                                fAccel += fIncBy;
                            }
                            if (fAccel > fATarget + 1) {
                                fAccel -= fIncBy;
                            }
                            if (fAccel > fATarget - 1 && fAccel < fATarget + 1) {
                                UpdateTargets();
                            }
                            fAngle = iAngle;
                            fXVel = (float) Math.cos(Math.toRadians(fAngle)) * fAccel * iUnit;
                            fYVel = (float) Math.sin(Math.toRadians(fAngle)) * fAccel * iUnit;
                            break;
                        case 4:
                            break;
                        case 5:
                            if (fAccel < fATarget - 1) {
                                fAccel += fIncBy;
                            }
                            fXVel = (float) Math.cos(Math.toRadians(fAngle)) * fAccel * iUnit;
                            break;
                    }
                }
                /*
                                iR = 1,
                                iP = 2,
                                iS = 3,
                                iBe = 4,
                                iBa = 5;
                                */
                public void Draw(SpriteBatch batch1) {
                    switch (iBulType) {
                        case 1:
                            DrawToRect(rBullet, imgBubble, batch1);
                            break;
                        case 2:
                            switch (pYou.iCostume) {
                                case 6:
                                    DrawToRect(rBullet, imgShuriken, batch1);
                                default:
                                    DrawToRect(rBullet, imgBullet, batch1);
                            }
                            break;
                        case 3:
                            DrawToRect(rBullet, imgBullet, batch1);
                            break;
                        case 4:
                            DrawToRect(rBullet, imgBullet, batch1);
                            break;
                        case 5:
                            DrawToRect(rBullet, imgMissile, batch1);
                            break;
                    }
                }

            }

        }/////////////////////////////
        //////////////////////////////
        class SoundPlayer {
            private Sound
                    sndCLick,
                    sndHiHat,
                    sndKnock,
                    sndKnockClick,
                    sndLiftDis,
                    sndLiftUp,
                    sndLowBAss,
                    sndWindSelected,
                    sndBeam,
                    sndColtShot,
                    sndImpact,
                    sndLoadShotGun,
                    sndLongSwitch,
                    sndRotarySwitch,
                    sndShortSwitch;
            private int iTimer = 0;

            private Boolean
                    bBassLine;
            private int iBassPattern1[] = {1,30,50,31,100,32,124,30,134,31,150,30},
                        iKeyPattern[] = {44,48,};

            public void Reset() {
                iTimer = 0;
            }
            public void Destroy() {
            }
            public void Setup() {
                        sndCLick = Gdx.audio.newSound(Gdx.files.internal("Sound/Click.ogg"));
                        sndHiHat = Gdx.audio.newSound(Gdx.files.internal("Sound/HiHatTouch.ogg"));
                        sndKnock = Gdx.audio.newSound(Gdx.files.internal("Sound/Knock.ogg"));
                        sndKnockClick = Gdx.audio.newSound(Gdx.files.internal("Sound/KnockClick.ogg"));
                        sndLiftDis = Gdx.audio.newSound(Gdx.files.internal("Sound/LiftDissapoint.ogg"));
                        sndLiftUp = Gdx.audio.newSound(Gdx.files.internal("Sound/LiftUp.ogg"));
                        sndLowBAss = Gdx.audio.newSound(Gdx.files.internal("Sound/LowBass.ogg"));
                        sndWindSelected = Gdx.audio.newSound(Gdx.files.internal("Sound/WindSelected.ogg"));
                        sndBeam = Gdx.audio.newSound(Gdx.files.internal("Sound/beam.mp3"));
                        sndColtShot = Gdx.audio.newSound(Gdx.files.internal("Sound/coltshot.mp3"));
                        sndImpact = Gdx.audio.newSound(Gdx.files.internal("Sound/impact.mp3"));
                        sndLoadShotGun = Gdx.audio.newSound(Gdx.files.internal("Sound/load shotgun.mp3"));
                        sndLongSwitch = Gdx.audio.newSound(Gdx.files.internal("Sound/longswitch.mp3"));
                        sndRotarySwitch = Gdx.audio.newSound(Gdx.files.internal("Sound/rotaryswitch.mp3"));
                        sndShortSwitch = Gdx.audio.newSound(Gdx.files.internal("Sound/shortswitch.mp3"));
            }

            public void Update() {
                iTimer++;
                if (bBassLine) {
                    for (int i = 0; i < iBassPattern1.length; i += 2) {
                        if (iBassPattern1[i] == iTimer) {
                            sndLiftUp.play(1,GetModifier(iBassPattern1[i+1]),.5f);
                        }
                    }
                }
                if (iTimer > 200) {
                    iTimer = 0;
                }
            }
            public void StopAll() {
            }

            public void DeprecateSound() {

            }
            public void StartBassLine() {
                bBassLine = true;
            }
            public void EndBassLine() {
                bBassLine = false;
            }

            public float GetModifier(Integer iKey) {
                float freq = (float)Math.pow(2,(iKey-49)/12) * 440f;
                return freq /261.626f;
            }

        }////////////////////DNF/
        //////////////////////////////
        class BoxRegulator {
            private int
                    iTimer,
                    iBCounter,
                    iFollow;
            private final int
                    iNorm = 0,
                    iWTF = 1,
                    iIce = 2,
                    iPrem = 3;
            private BoxGenerator
                    BoxGen[] = new BoxGenerator[4];
            public Texture
                    imgGreenDrop[],
                    imgNormBox[],
                    imgBomb[],
                    imgRands[],
                    imgPenguins[];

            private Boolean
                    bFirstPassed = false;

            public void Nuke() {
                for (BoxGenerator Boxes: BoxGen) {
                    Boxes.Nuke();
                }
            }
            public void Setup() {
                for (int i = 0; i < BoxGen.length; i++) {
                    BoxGen[i] = new BoxGenerator();
                    BoxGen[i].Setup();
                    BoxGen[i].RackEmUp(ScreenHeight - (10 * iUnit),(10 * iUnit), i * (50 * iUnit + (((ScreenY - (10 * iUnit) - 10 * iUnit) / 10) * 3)), 50 * iUnit);
                }
                imgGreenDrop = new Texture[3];
                imgNormBox = new Texture[3];
                imgBomb = new Texture[2];
                imgRands = new Texture[10];
                imgPenguins = new Texture[3];

                imgGreenDrop[0] = new Texture("Crates/gBoxD1.jpg");
                imgGreenDrop[1] = new Texture("Crates/boxG.jpg");
                imgGreenDrop[2] = new Texture("Crates/GreenDrop.png");

                imgNormBox[0] = new Texture("Crates/lBox.jpg");
                imgNormBox[1] = new Texture("Crates/lBoxD1.jpg");
                imgNormBox[2] = new Texture("Crates/lBoxD2.jpg");

                imgBomb[0] = new Texture("Crates/BoxBomb.png");
                imgBomb[1] = new Texture("Crates/BoxBomb2.png");

                imgPenguins[0] = new Texture("Crates/SmallPenguin.png");
                imgPenguins[1] = new Texture("Crates/SmallpengFlat.png");
                imgPenguins[2] = new Texture("Crates/SmallpengGold.png");

                imgRands[0] = new Texture("Crates/orange.png");
                imgRands[1] = new Texture("Crates/boxempty2.jpg");
                imgRands[2] = new Texture("Crates/blackbook.png");
                imgRands[3] = new Texture("Crates/boxempt.png");
                imgRands[4] = new Texture("Crates/boxgcity.png");
                imgRands[5] = new Texture("Crates/boxspeak.png");
                imgRands[6] = new Texture("Crates/boxspeaklarge.png");
                imgRands[7] = new Texture("Crates/FrostMan.png");
                imgRands[8] = new Texture("Crates/pinkWtf.png");
                imgRands[9] = new Texture("Crates/RedPlainBox.png");


                iFollow = iNorm;
            }
            public void Destroy() {
                imgGreenDrop[0].dispose();
                imgGreenDrop[1].dispose();
                imgGreenDrop[2].dispose();

                imgNormBox[0].dispose();
                imgNormBox[1].dispose();
                imgNormBox[2].dispose();

                imgBomb[0].dispose();
                imgBomb[1].dispose();

                imgPenguins[0].dispose();
                imgPenguins[1].dispose();
                imgPenguins[2].dispose();

                imgRands[0].dispose();
                imgRands[1].dispose();
                imgRands[2].dispose();
                imgRands[3].dispose();
                imgRands[4].dispose();
                imgRands[5].dispose();
                imgRands[6].dispose();
                imgRands[7].dispose();
                imgRands[8].dispose();
                imgRands[9].dispose();
            }
            public void Resume() {
            }
            public void DrawBoxes(SpriteBatch batch1, Boolean bWillTest2) {
                for (BoxGenerator aBoxGen : BoxGen) {
                    aBoxGen.drawBoxes(batch1, bWillTest2);
                }
            }

            public void ResetBoxes() {
                for (int i = 0; i < BoxGen.length; i++) {
                    BoxGen[i].RackEmUp(ScreenHeight - (10 * iUnit),(10 * iUnit),  ScreenWidth + (i * 75 * iUnit), 50 * iUnit);
                }
                iBCounter = 0;
                iTimer = 0;
                bFirstPassed = false;
                iFollow = iNorm;
            }

            public void MoveLeft(float fLeft) {
                if (!bPaused) {
                    for (int i = 0, boxGenLength = BoxGen.length; i < boxGenLength; i++) {

                        BoxGen[i].MoveLeft(fLeft);
                        if (BoxGen[i].bGone) {
                            BoxGen[i].RackEmUp(ScreenHeight - (10 * iUnit), (10 * iUnit), ScreenWidth + (50 * iUnit), 50 * iUnit);
                            iBCounter++;
                            iTimer = 0;
                            if (iBCounter == 4) {
                                iBCounter = 0;
                            }
                        }
                        iTimer++;
                    }
                }
            }

            class BoxGenerator {//------------------------------------------------------------
                private Integer iNumToDraw,
                        iSBox, iNBox, iLBox, iSpaceLeft, iDifference,
                        iBM1,
                        iBM2,
                        iBM3;
                public Integer iTypeBoxes;
                public Random rand = new Random(); /*-->Why Aren't you in CLASS?*/
                public Rect
                        rBoxes[] = new Rect[10],
                        rDrawBoxes[] = new Rect[20],
                        rImp = new Rect(),
                        rPlayerDetect = new Rect();
                private Boolean
                        bGone = false;
                private float fLeft,fRight;
                private ImpSign
                        isImp[],
                        iDupS[];
                private Box
                        Boxes[];

                public void ShowSign(float fX, float fY) {
                    iDupS = isImp; //Shoot Bullet
                    isImp = new ImpSign[isImp.length + 1];
                    System.arraycopy(iDupS, 0, isImp, 0, iDupS.length);
                    isImp[isImp.length - 1] = new ImpSign();
                    isImp[isImp.length - 1].Setup();
                    isImp[isImp.length - 1].ShowImp(fX, fY);
                }

                public void DeprecateSign(Integer i) {
                    iDupS = isImp;
                    isImp = new ImpSign[isImp.length - 1];
                    for (int j = 0; j < isImp.length; j++) {
                        if (j < i) {
                            isImp[j] = iDupS[j];
                        } else {
                            isImp[j] = iDupS[j + 1];
                        }
                    }
                }

                public void Setup() {

                    isImp = new ImpSign[1];
                    isImp[0] = new ImpSign();
                    isImp[0].Setup();
                    Boxes = new Box[10];
                    for (int i = 0; i < Boxes.length; i++) {
                        Boxes[i] = new Box();
                    }
                }

                private void SetBoxSize(float Top, float Bottom) {
                    iDifference = Math.round(Top - Bottom);
                    iSBox = iDifference / 10;
                    iNBox = iDifference / 5;
                    iLBox = ((iDifference / 10) * 3);
                    iSpaceLeft = iDifference;
                    iNumToDraw = 0;
                    for (int i = 0; i < Boxes.length; i++) {
                        if (iSpaceLeft >= iSBox) {
                            iSpaceLeft -= Math.round(Boxes[i].Setup(fLeft + rand.nextInt(Math.round(50 * iUnit)), Bottom + iSpaceLeft, iSpaceLeft, 0));
                            iNumToDraw++;
                        } else {
                            Boxes[i].Setup(0,0, 0, 2);
                        }
                    }
                }

                public void Nuke() {
                    for (int i = 0; i < Boxes.length; i++) {
                        Boxes[i].iHitCount = 0;
                    }
                }

                public void MoveLeft(float Leftp) {
                    fLeft -= Leftp * (8);
                    fRight = (-50*iUnit);
                    for (int i = 0; i < Boxes.length ; i++) {
                        Boxes[i].update(fLeft);
                        if (Boxes[i].rDisplay.right > fRight) {
                            fRight = Boxes[i].rDisplay.right;
                        }
                    }
                    if (fLeft + (80*iUnit) < 0) {
                        bGone = true;
                    }
                }

                public void RackEmUp(float Top, float Bottom, float Left, float Margin) {
                    iTypeBoxes = iFollow;
                    bGone = false;
                    fLeft = Left;
                    SetBoxSize(Top, Bottom);
                   // SetLeft(Left, Margin);
                }

                public void drawBoxes(SpriteBatch batch1, Boolean bWillTest) {
                    for (int i = 1; i < isImp.length; i++) {
                        isImp[i].Draw(batch1);
                        if (!isImp[i].bShowing) {
                            DeprecateSign(i);
                        }
                    }

                    for (int i = 0; i < Boxes.length ; i++) {
                        Boxes[i].Draw(batch1);
                            if (i < iNumToDraw) {
                                 /*
                                iR = 1,
                                iP = 2,
                                iS = 3,
                                iBe = 4,
                                iBa = 5;
                                 */
                                rPlayerDetect.RectCopy(pYou.PlayerRect);
                                rPlayerDetect.top += pYou.PlayerWidth;
                                rPlayerDetect.bottom = rPlayerDetect.top - pYou.PlayerWidth;
                                if ((Boxes[i].CollissionDetect(rPlayerDetect) && bWillTest)) {
                                    pYou.OnImpact();
                                    Boxes[i].Die();
                                }
                                if (Boxes[i].CollissionDetect(pYou.rBeamRect)) {
                                    Boxes[i].Die();
                                }
                                for (int j = 0; j < AK47.bMag.length; j++) {
                                    if (AK47.iBulType == 5) {
                                        rImp.left = (AK47.bMag[j].rBullet.left - (20 * iUnit));
                                        rImp.right = (AK47.bMag[j].rBullet.right + (20 * iUnit));
                                        rImp.top = (AK47.bMag[j].rBullet.top + (20 * iUnit));
                                        rImp.bottom = (AK47.bMag[j].rBullet.bottom - (20 * iUnit));
                                    }
                                    if (Boxes[i].CollissionDetect(AK47.bMag[j].rBullet)) {
                                        if (AK47.iBulType == 5) {
                                            for (int a = 0; a < iNumToDraw; a++) {
                                                if (Boxes[a].CollissionDetect(rImp)) {
                                                    Boxes[a].Die();
                                                }
                                            }
                                            if ( AK47.bMag.length > 0) ShowSign(AK47.bMag[j].rBullet.right,AK47.bMag[j].rBullet.CenterY());
                                        }



                                        AK47.DeprecateBullet(j);
                                        Boxes[i].DecHitCount(1);

                                        if (bVibrate) Gdx.input.vibrate(30);
                                    }
                                }
                            }


                    }

                }

                public void Destroy() {

                }

                class Box {
                    private Rect
                            rBounds = new Rect(),
                            rDisplay = new Rect(),
                            rCollision = new Rect(),
                            rFromLine = new Rect(),
                            rBomb = new Rect(),
                            rColision2 = new Rect();
                    private Integer iType,iImgNum, iHitCount, iAnimCount,iAnimMAx,iBombRadius;
                    private float fWidth,fSpeed;
                    private Boolean
                            bAnimForwards,bActive,bBomb;    //bBomb = bDrip
                    private final int
                            iLargeBox = 1,
                            iMedBox = 2,
                            iSmallBox = 3,
                            iLargeSmallBox = 4,
                            iMedExplodingbox = 5,
                            iSmallMusicBox = 6,
                            iLargePenguin20Box = 7,
                            iLargePenguin40 = 8,
                            iLargePenguinLying = 9,
                            iGreenStuff = 10;
                    private XplodeBox
                            xplode;


                    public float Setup(float fLeft, float fTop, Integer fSpaceLeft, Integer iRoundEndNum) {
                        iType = 0;
                        iHitCount = 0;
                        rBounds.OffScreen();
                        rColision2.OffScreen();
                        rFromLine.top = fTop;
                        rFromLine.left = fLeft;
                        rFromLine.bottom = fTop - fWidth;
                        rFromLine.right = fLeft + fWidth;

                        iSBox = iDifference / 10;
                        iNBox = iDifference / 5;
                        iLBox = ((iDifference / 10) * 3);
                        iType = 0;
                        Integer iSB[] = new Integer[]{3,6};
                        Integer iMB[] = new Integer[]{2,5};
                        Integer iLB[] = new Integer[]{2,5,3,6,1,4,7,8,9};
                        iImgNum = -1;
                        switch (fSpaceLeft / iSBox) {
                            case 10:case 9:case 8:case 7:case 6:case 5:case 4:case 3:
                                Integer iChose = rand.nextInt(13)+1;
                                if ((iChose <=7)) {
                                    iType = rand.nextInt(4) + 1;
                                    if (iType == 1) {
                                        if (rand.nextInt(3) == 0) {
                                            iType = iGreenStuff;
                                            iImgNum = rand.nextInt(2);
                                        }
                                    } else if (rand.nextInt(13) > 10 && iType > 1) {
                                        iImgNum  = rand.nextInt(10);
                                    }
                                } else if ((iChose == 8)) {
                                    iType = iMedExplodingbox;
                                    iImgNum = 0;
                                } else if ((iChose == 9)) {
                                    iType = rand.nextInt(3) + 7;
                                } else if ((iChose > 9 )) {
                                    iType = iSmallMusicBox;
                                } else {
                                    iType = iSmallMusicBox;
                                }
                                break;
                            case 2:
                                Integer iChose2 = rand.nextInt(13)+1;
                                if ((iChose2 <=7)) {
                                    iType = rand.nextInt(2) + 2;
                                } else if ((iChose2 == 8) && rand.nextInt(2) == 1) {
                                    iType = iMedExplodingbox;
                                } else if ((iChose2 > 9 )) {
                                    iType = iSmallMusicBox;
                                }
                                break;
                            case 1:default:
                                Integer iChose3 = rand.nextInt(13)+1;

                                iType = iSmallBox;
                                if ((iChose3 > 9 )) {
                                    iType = iSmallMusicBox;
                                }
                                break;
                            case 0:
                                fWidth = 0;
                                break;
                        }
                        iAnimMAx = 100;
                        iHitCount = 1;
                        if (SK.iDiffLevel == 0) {
                            if (rand.nextInt(3) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                            if (iType == 5) {
                                iType = 2;
                            }
                            if (rand.nextInt(5) == 1) {
                                iHitCount = 0;
                            }
                        }
                        if (SK.iDiffLevel == 1) {
                            if (rand.nextInt(3) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                        }

                        if (SK.iDiffLevel == 2) {
                            if (rand.nextInt(3) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                        }
                        if (SK.iDiffLevel == 3) {
                            if (rand.nextInt(3) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                        }
                        if (SK.iDiffLevel == 4) {
                            if (rand.nextInt(2) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                        }
                        if (SK.iDiffLevel == 5) {
                            if (rand.nextInt(2) == 1) {
                                iType = 3;
                                iHitCount = 1;
                            }
                        }
                        if (SK.iDiffLevel == 6) {
                                iType = 3;
                            iHitCount = 1;
                        }
                        if (SK.iDiffLevel == 7) {
                                iType = 3;
                            iHitCount = 2;
                        }
                        if (SK.iDiffLevel == 8) {
                                iType = 3;
                            iHitCount = 2;
                        }
                        if (SK.iDiffLevel == 9) {
                                iType = 5;
                            iHitCount = 1;
                        }
                        if (SK.iDiffLevel == 10) {
                                iType = 5;
                            iHitCount = 2;
                        }


                        switch (iType) {
                            case 1:
                                fWidth = iLBox;
                                iHitCount = 2;
                                break;
                            case 2:
                                fWidth = iNBox;
                                iHitCount = 2;
                                break;
                            case 3:
                                fWidth = iSBox;
                                iHitCount = 2;
                                break;
                            case 4:
                                fWidth = iLBox;
                                iHitCount = 2;
                                break;
                            case 5:
                                fWidth = iNBox;
                                iImgNum = 0;
                                iAnimMAx = rand.nextInt(30) + 100;
                                iHitCount = 2;
                                xplode = new XplodeBox();
                                xplode.Reset(fLeft,fTop,fWidth,fSpaceLeft,0);
                                break;
                            case 6:
                                fWidth = iSBox;
                                iAnimMAx = 10;
                                iHitCount = 2;
                                break;
                            case 7:
                                fWidth = iLBox;
                                iHitCount = 2;
                                break;
                            case 8:
                                fWidth = iLBox;
                                iHitCount = 2;
                                break;
                            case 9:
                                fWidth = iLBox;
                                iHitCount = 2;
                                break;
                            case 10:
                                fWidth = iLBox;
                                iAnimMAx = rand.nextInt(25) + 30;
                                iHitCount = 2;
                                break;
                            case 0:
                                fWidth = 0;
                                iHitCount = 0;
                                iHitCount = 2;
                                break;
                        }
                        rFromLine.left = (rand.nextInt(50) * iUnit);
                        rFromLine.top = fTop;
                        rFromLine.right = rFromLine.left + fWidth;
                        rFromLine.bottom = rFromLine.top - fWidth;
                        rBounds.RectCopy(rFromLine);
                        rBounds.left = rFromLine.left + fLeft;
                        rBounds.right = rBounds.left + fWidth;
                         /*     iLargeBox = 1,
                                    iMedBox = 2,
                                    iSmallBox = 3,
                                    iLargeSmallBox = 4,
                                    iMedExplodingbox = 5,
                                    iSmallMusicBox = 6,
                                    iLargePenguin20Box = 7,
                                    iLargePenguin40 = 8,
                                    iLargePenguinLying = 9,
                                    iGreenStuff = 10;*/
                        switch (iType) {
                            case 1:
                                rDisplay.RectCopy(rBounds);
                                rCollision.RectCopy(rBounds);
                                iHitCount = 2;
                                break;
                            case 2:
                                rDisplay.RectCopy(rBounds);
                                rCollision.RectCopy(rBounds);
                                iHitCount = 1;
                                break;
                            case 3:
                                rDisplay.RectCopy(rBounds);
                                rCollision.RectCopy(rBounds);

                                break;
                            case 4:
                                rDisplay.right = rBounds.right;
                                rDisplay.left = rDisplay.right - iSBox;
                                rDisplay.bottom = rBounds.bottom;
                                rDisplay.top = rBounds.bottom + iSBox;
                                iHitCount = 1;
                                break;
                            case 5:
                                break;
                            case 6:
                                rDisplay.RectCopy(rBounds);
                                rCollision.RectCopy(rBounds);
                                iHitCount = 1;
                                break;
                            case 7:
                                rDisplay.left = rBounds.CenterX() - (2* iUnit);
                                rDisplay.top = rBounds.bottom + (8*iUnit);
                                rDisplay.right = rDisplay.left + iSBox;
                                rDisplay.bottom = rBounds.bottom;
                                iHitCount = 1;
                                break;
                            case 8:
                                rDisplay.left = rBounds.CenterX() - (2* iUnit);
                                rDisplay.top = rBounds.bottom + (8*iUnit);
                                rDisplay.right = rDisplay.left + (4*iUnit);
                                rDisplay.bottom = rBounds.bottom;
                                iHitCount = 1;
                                break;
                            case 9:
                                rDisplay.left = rBounds.CenterX() - (4* iUnit);
                                rDisplay.top = rBounds.bottom + (4*iUnit);
                                rDisplay.right = rDisplay.left + (8*iUnit);
                                rDisplay.bottom = rBounds.bottom;
                                iHitCount = 1;
                                break;
                            case 10:
                                iImgNum = (rand.nextInt(2));
                                if (iImgNum == 1) {
                                    iHitCount = 1;
                                } else {
                                    iHitCount = 2;
                                }
                                rDisplay.RectCopy(rBounds);
                                rCollision.RectCopy(rBounds);
                                iHitCount = 1;
                                break;
                        }
                        iAnimCount = 0;
                        fSpeed = rand.nextInt(100) * 0.01f;
                        if (iRoundEndNum == 2) {
                            fSpeed = 8.4f;
                        }

                        bActive = true;
                        bAnimForwards = true;
                        bBomb = false;
                        rBomb.OffScreen();
                        iBombRadius = 0;
                        if (SK.iDiffLevel == 0) {
                            if (rand.nextInt(4) == 1) {
                                iHitCount = 0;
                            }
                        }
                        if (SK.iDiffLevel == 1) {
                            if (rand.nextInt(5) == 1) {
                                iHitCount = 0;
                            }
                        }
                        if (SK.iDiffLevel == 2) {
                            if (rand.nextInt(6) == 1) {
                                iHitCount = 0;
                            }
                        }
                        if (SK.iDiffLevel == 3) {
                            if (rand.nextInt(7) == 1) {
                                iHitCount = 0;
                            }
                        }
                        return rBounds.top - rBounds.bottom;
                    }

                    public void DecHitCount(Integer iMuch) {
                        if (iType ==5) {
                            xplode.DecHitCount(iMuch);
                        } else {
                            iHitCount -= iMuch;
                        }
                    }
                    public void update(float fLeftP) {
                        if (!bPaused ) {
                            if (iHitCount <= 0) bActive = false;
                                rBounds.left = rFromLine.left + fLeftP;
                                rBounds.right = rBounds.left + fWidth;
                                rFromLine.left -= fSpeed;
                            if (rBounds.right < 0) bActive = false;
                            if (iAnimCount > iAnimMAx) {
                                bAnimForwards = false;
                            } else if (iAnimCount < 1) {
                                bAnimForwards = true;
                            }
                            if (bAnimForwards) {
                                iAnimCount++;
                            } else if (!bAnimForwards) {
                                iAnimCount--;
                            }
                            switch (iType) {
                                case 1:
                                    rDisplay.RectCopy(rBounds);
                                    rCollision.RectCopy(rBounds);
                                    break;
                                case 2:
                                    rDisplay.RectCopy(rBounds);
                                    rCollision.RectCopy(rBounds);
                                    break;
                                case 3:
                                    rDisplay.RectCopy(rBounds);
                                    rCollision.RectCopy(rBounds);
                                    break;
                                case 4:
                                    fWidth = iLBox;
                                    rDisplay.bottom = rBounds.bottom + (((iLBox - iSBox) / 100) * iAnimCount);
                                    rDisplay.top = rDisplay.bottom + iSBox;
                                    rDisplay.left = rBounds.right - iSBox - (iLargeBox / 100 * iAnimCount);
                                    rDisplay.right = rDisplay.left + iSBox;
                                    rCollision.RectCopy(rDisplay);
                                    break;
                                case 5:
                                    xplode.Update(fLeftP);
                                    break;
                                case 6:
                                    rDisplay.RectCopy(rBounds);
                                    rCollision.RectCopy(rBounds);
                                    break;
                                case 7:
                                    rDisplay.left = rBounds.CenterX() - (2 * iUnit);
                                    rDisplay.top = rBounds.bottom + (8 * iUnit);
                                    rDisplay.right = rDisplay.left + (4 * iUnit);
                                    rDisplay.bottom = rBounds.bottom;
                                    rCollision.RectCopy(rDisplay);
                                    break;
                                case 8:
                                    rDisplay.left = rBounds.CenterX() - (4 * iUnit);
                                    rDisplay.top = rBounds.bottom + (4 * iUnit);
                                    rDisplay.right = rDisplay.left + (8 * iUnit);
                                    rDisplay.bottom = rBounds.bottom;
                                    rCollision.RectCopy(rDisplay);
                                    break;
                                case 9:
                                    rDisplay.left = rBounds.CenterX() - (2 * iUnit);
                                    rDisplay.top = rBounds.bottom + (8 * iUnit);
                                    rDisplay.right = rDisplay.left + (4 * iUnit);
                                    rDisplay.bottom = rBounds.bottom;
                                    rCollision.RectCopy(rDisplay);

                                    break;
                                case 10:
                                    rDisplay.RectCopy(rBounds);
                                    rCollision.RectCopy(rBounds);
                                    if (iAnimCount == 10 && iHitCount == 1 && !bBomb) {
                                        bBomb = true;
                                        iBombRadius = 0;
                                        rColision2.left = rDisplay.CenterX() - (iSBox/2);
                                        rColision2.right = rDisplay.CenterX() + (iSBox/2);
                                        rColision2.top = rDisplay.bottom;
                                        rColision2.bottom = rColision2.top - iSBox + (2* iUnit);
                                    }
                                    if (bBomb) {
                                        rColision2.left = rDisplay.CenterX() - (iSBox/2);
                                        rColision2.right = rDisplay.CenterX() + (iSBox/2);
                                        rColision2.top -= iUnit * (iBombRadius * 0.1f);
                                        rColision2.bottom = rColision2.top - (iSBox + (4* iUnit));
                                        if (rColision2.top < 0){
                                            bBomb = false;
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                    public void Draw(SpriteBatch batch1) {
                        if (CollisionTest(rFullScreen,rDisplay) && bActive) {
                             /*     iLargeBox = 1,
                                    iMedBox = 2,
                                    iSmallBox = 3,
                                    iLargeSmallBox = 4,
                                    iMedExplodingbox = 5,
                                    iSmallMusicBox = 6,
                                    iLargePenguin20Box = 7,
                                    iLargePenguin40 = 8,
                                    iLargePenguinLying = 9,
                                    iGreenStuff = 10;*/
                            switch (iType) {
                                case 1:
                                    switch (iHitCount) {
                                        case 1:
                                            DrawToRect(rDisplay,imgNormBox[2],batch1);
                                            break;
                                        case 2:
                                            DrawToRect(rDisplay,imgNormBox[1],batch1);
                                            break;
                                        case 3:
                                            DrawToRect(rDisplay,imgNormBox[0],batch1);
                                            break;
                                    }
                                    break;
                                case 2:
                                    if (iImgNum != -1) {
                                        DrawToRect(rDisplay,imgRands[iImgNum],batch1);
                                    } else switch (iHitCount) {
                                        case 1:
                                            DrawToRect(rDisplay,imgNormBox[2],batch1);
                                            break;
                                        case 2:
                                            DrawToRect(rDisplay,imgNormBox[0],batch1);
                                            break;
                                    }
                                    break;
                                case 3:
                                    DrawToRect(rDisplay,imgNormBox[0],batch1);
                                    break;
                                case 4:
                                    DrawToRect(rDisplay,imgNormBox[0],batch1);
                                    break;
                                case 5:
                                    break;
                                case 6:
                                    if (bSound) {
                                        if (iAnimCount < 2) {
                                            DrawToRect(rDisplay, imgRands[6], batch1);
                                        } else {
                                            DrawToRect(rDisplay, imgRands[5], batch1);
                                        }
                                    } else DrawToRect(rDisplay, imgRands[5], batch1);

                                    break;
                                case 7:
                                    DrawToRect(rDisplay,imgPenguins[0],batch1);
                                    break;
                                case 8:
                                    DrawToRect(rDisplay,imgPenguins[1],batch1);
                                    break;
                                case 9:
                                    DrawToRect(rDisplay,imgPenguins[2],batch1);
                                    break;
                                case 10:
                                     {
                                        DrawToRect(rDisplay, imgGreenDrop[iImgNum], batch1);
                                        if (iHitCount > 0)iImgNum = iHitCount - 1;
                                        if (bBomb) {
                                            iBombRadius++;
                                            DrawToRect(rColision2, imgGreenDrop[2], batch1);
                                        }
                                    }
                                    break;
                            }
                        }
                        if (iType == 5) {
                            xplode.Draw(batch1);
                        }
                       /* batch1.setColor(1,1,1,0.1f);
                        Rect Debug = new Rect();
                        Debug.equals(ScreenX/2,rDisplay.top,rDisplay.right,rDisplay.bottom);
                        DrawToRect(Debug,imgBlack,batch1);
                        batch1.setColor(1,1,1,1);*/
                    }
                    public Boolean CollissionDetect(Rect r) {
                        if (iType != 5) {
                            switch (iType) {
                                case 7:
                                    if (CollisionTest(r, rCollision) && bActive) {
                                        pYou.ShowSign("+10", rCollision.CenterX(), rCollision.CenterY(), 180);
                                        SK.IncScore(10);
                                        iHitCount = 0;
                                        return false;
                                    }
                                case 8:
                                    if (CollisionTest(r, rCollision) && bActive) {
                                        pYou.ShowSign("+20", rCollision.CenterX(), rCollision.CenterY(), 180);
                                        SK.IncScore(20);
                                        iHitCount = 0;
                                        return false;
                                    }
                                case 9:
                                    if (CollisionTest(r, rCollision) && bActive) {
                                        pYou.ShowSign("+50", rCollision.CenterX(), rCollision.CenterY(), 180);
                                        SK.IncScore(50);
                                        iHitCount = 0;
                                        return false;
                                    }
                                case 10:
                                    if ((CollisionTest(r, rColision2)) && bActive) {
                                        iHitCount = 0;
                                        pYou.ShowSign("Nice +100", rColision2.left, rColision2.top, 30);
                                        SK.AddCash(100);
                                        return true;
                                    }
                            }
                            if (bBomb) {
                                return (CollisionTest(r, rCollision) || CollisionTest(r, rColision2)) && bActive;
                            } else {
                                return CollisionTest(r, rCollision) && bActive;
                            }
                        } else {
                            return xplode.ImpactTest(r);
                        }
                    }
                    public void Die() {
                        if (iType != 5) {
                            iHitCount = 0;
                        } else {
                            xplode.Die();
                        }
                    }
                    public void Destroy() {

                    }
                }
                class XplodeBox {
                    private Rect
                            rDisplay = new Rect(),
                            rDisplay2 = new Rect(),
                            rLeftPos = new Rect();
                    private Boolean
                            bExploding,
                            bWarning,
                            bActive;
                    private Integer
                            iHitCount,iDifference,iAnim,iWait,iWarnWait,iAlphaAnim;
                    private float fSize,fLeftPlus;

                    public void Die() {
                        iHitCount = 0;
                    }
                    public float Reset(float fLeft, float fTop,float fSizeP, Integer fSpaceLeft, Integer iRoundEndNum) {
                        bExploding = false;
                        bWarning = false;
                        bActive = true;
                        fLeftPlus = rand.nextInt(50) *iUnit;
                        fSize = fSizeP;
                        rDisplay.equals(fLeftPlus + fLeft,fTop,fLeftPlus+fLeft + fSizeP,fTop - fSizeP);
                        rDisplay2.CopySquare(rDisplay,5*iUnit);
                        iWait = rand.nextInt(120) + 240;
                        iWarnWait = iWait + rand.nextInt(60) + 120;
                        iAlphaAnim = 0;
                        iAnim = 0;
                        iHitCount = 1;
                        return rDisplay.top - rDisplay.bottom;
                    }
                    public Boolean ImpactTest(Rect r) {
                        if (bActive) {
                            if (bExploding)
                                return CollisionTest(r, rDisplay) || CollisionTest(r, rDisplay2);
                            return CollisionTest(r, rDisplay);
                        } else return false;
                    }
                    public void DecHitCount(Integer iMuch) {
                        iHitCount -= iMuch;
                        if (iHitCount < 0) iHitCount = 0;
                    }
                    public void Update(float fLeft) {
                        rDisplay.left = fLeft + fLeftPlus;
                        rDisplay.right = rDisplay.left + fSize;
                        if (iWarnWait > 0) iWarnWait--;
                        if (iWait > 0) iWait--;
                        else bWarning = true;
                        if (iWarnWait == 0) bExploding = true;
                        if (iHitCount <= 0) {
                            bActive = false;
                            bExploding = false;
                            iHitCount = 0;
                        }
                        if (bWarning) {
                            iAlphaAnim += 4;
                            if (iAlphaAnim > 176) iAlphaAnim = 0;
                        }
                        if (bExploding) {
                            rDisplay2.CopySquare(rDisplay2,iAnim * iUnit * 0.01f);
                            rDisplay2.top = rDisplay.top;
                            rDisplay2.bottom = rDisplay.bottom;
                            bWarning = false;
                            iAnim++;
                            if (rDisplay2.left < rDisplay.left - 50*iUnit ) {
                                iHitCount = 0;
                            }
                        }

                    }
                    public void Draw(SpriteBatch batch1) {
                        if (bActive) {
                            if (CollisionTest(rDisplay,rFullScreen)) {
                                DrawToRect(rDisplay,imgBomb[0],batch1);
                            }
                            if (bWarning) {
                                batch1.setColor(1,1,1,(float)Math.sin(Math.toRadians(iAlphaAnim)));
                                DrawToRect(rDisplay,imgBomb[1],batch1);
                                batch1.setColor(1,1,1,1);
                            }
                            if (bExploding) {
                                DrawToRect(rDisplay2,imgSun,batch1);
                            }
                        }
                    }
                    public void Destroy() {

                    }

                }
                class DripBox{
                    private Rect
                            rDisplay = new Rect(),
                            rDisplay2 = new Rect(),
                            rLeftPos = new Rect();
                    private Boolean
                            bExploding,
                            bActive;
                    private Integer
                            iHitCount,iDifference,iAnim,iWait,iAlphaAnim;
                    private float fSize,fLeftPlus;

                    public void Die() {
                        iHitCount = 0;
                    }
                    public float Reset(float fLeft, float fTop,float fSizeP, Integer fSpaceLeft, Integer iRoundEndNum) {
                        bExploding = false;
                        fSize = fSizeP;
                        bActive = true;
                        fLeftPlus = rand.nextInt(50) *iUnit;
                        rDisplay.equals(fLeftPlus + fLeft,fTop,fLeftPlus+fLeft + fSize,fTop - fSize);
                        rDisplay2.CopySquare(rDisplay,10*iUnit);
                        bActive = true;
                        iWait = 50;
                        iAlphaAnim = 0;
                        iAnim = 0;
                        iHitCount = 1;
                        return rDisplay.top - rDisplay.bottom;
                    }
                    public Boolean ImpactTest(Rect r) {
                        if (bActive) {
                            if (bExploding)
                                return CollisionTest(r, rDisplay) || CollisionTest(r, rDisplay2);
                            return CollisionTest(r, rDisplay);
                        } else return false;
                    }
                    public void DecHitCount(Integer iMuch) {
                        iHitCount -= iMuch;
                        if (iHitCount < 0) iHitCount = 0;
                    }
                    public void Update(float fLeft) {
                        rDisplay.left = fLeft + fLeftPlus;
                        rDisplay.right = rDisplay.left + fSize;
                        if (iAnim == 0) {
                            iAnim = 100;
                        }
                        iAnim--;
                        if (iAnim == iWait && !bExploding) {
                            bExploding = true;
                            rDisplay2.CopySquare(rDisplay,10*iUnit);
                            rDisplay2.MoveDown(rDisplay.height());
                        }
                        if (iHitCount <= 0) {
                            bActive = false;
                            bExploding = false;
                            iHitCount = 0;
                        }
                        if (bExploding) {
                            rDisplay2.CopySquare(rDisplay,10*iUnit);
                            rDisplay2.MoveDown(0.01f*iUnit);
                            if (rDisplay2.top < 0) {
                                bExploding = false;
                            }
                        }

                    }
                    public void Draw(SpriteBatch batch1) {
                        if (bActive) {
                            if (CollisionTest(rDisplay,rFullScreen)) {
                                DrawToRect(rDisplay,imgBomb[0],batch1);
                            }
                            if (bExploding) {
                                DrawToRect(rDisplay2,imgSun,batch1);
                            }
                        }
                    }
                    public void Destroy() {

                    }

                }
                class ImpSign {
                    private Boolean
                            bShowing = false;
                    private Integer
                            iSignCount = 0;

                    private float fX,fY;
                    private Rect
                            rExplosion = new Rect();

                    public void Setup() {
                    }
                    public void Destroy() {


                    }
                    public void ShowImp(float fXP, float fYP) {
                        fX = fXP;
                        fY = fYP;
                        bShowing = true;
                        rExplosion.equals(fX - (iSignCount* iUnit),fY + (iSignCount* iUnit),fX + (iSignCount* iUnit),fY - (iSignCount* iUnit));
                        iSignCount = 50;
                    }

                    public void Draw(SpriteBatch batch1) {
                        rExplosion.equals(fX - (iSignCount* iUnit*0.5f),fY + (iSignCount* iUnit*0.5f),fX + (iSignCount* iUnit*0.5f),fY - (iSignCount* iUnit*0.5f));
                        if (iSignCount > 0) {
                            iSignCount--;
                            DrawToRect(rExplosion,imgSun,batch1);
                        } else {
                            bShowing = false;
                        }
                    }
                }
            }////(C14tor)///////////
        }///////////////////////
        //////////////////////////////
    }   ///////////////////////////////////////////////

}