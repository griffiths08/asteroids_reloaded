import processing.core.PApplet;
import processing.net.*;
import processing.sound.*;
import processing.video.*;
//**********************
boolean Sound = true;
//**********************
//********************************************
//VARS_DIR
// bt = Shader half% Redraw /0
// init&state = Load Screen Logic
// ch&mold = Mouse Logic

boolean In_Local_Game, In_Net_Game, Is_Server, Load_Done, Paused, bt, init, state, ch = false, mold = false, Reset_Game=false;
StringList Keys = new StringList();
//int Gfx
ArrayList<Player> Players = new ArrayList<Player>();
ArrayList<Asteroid> Asteroids = new ArrayList<Asteroid>();
ArrayList<Projectile> Projectiles = new ArrayList<Projectile>();
ArrayList<Particle_System> Particle_Systems = new ArrayList<Particle_System>();
ArrayList<exp> ex = new ArrayList<exp>();
ArrayList<Dialog> Dialog_Boxs = new ArrayList<Dialog>();
PShader Galaxy, Title_Shader, Grid_Generator, Scanline_Generator;
PImage GamePad_Icon, Network_Icon, Back_Icon;
PFont Menu_Font, Menu_Font2, Font1, Logo_Font;
int Menu_Ani, High_Score=0, ow, oh, Game_Mode;
PShape Asteroid, Keyboard;
PGraphics pg, out, Playfield, grid, hud;
float pbangle, ani1, ani2, ra3 = 0, cam = 100, Time, bounds;
PVector Scale = new PVector(1, 1), L1 = new PVector(), L2 = new PVector(), L1_Dir = new PVector(), L2_Dir = new PVector(), size = new PVector(1024*1, 768*1);
ArrayList<PVector> circle = new ArrayList<PVector>(), square = new ArrayList<PVector>(), morph = new ArrayList<PVector>();
SoundFile Fire, th, T1, T2, T3, T4;
WhiteNoise n1;
public PApplet parent = this;
Movie Info_Movie;
void movieEvent(Movie m) {
  m.read();
}
void Load() { //Load Game assts
  // Load Fonts
  Menu_Font = createFont("Fonts/SourceSansPro-Regular.ttf", 20);
  Menu_Font2 = createFont("Fonts/ka1.ttf", 75);
  Font1 = loadFont("Fonts/01_Digit-48.vlw");
  Logo_Font = loadFont("Fonts/Arenq-200.vlw");
  //Clean
  Back_Icon = loadImage("Back.png"); //Clean
  GamePad_Icon = loadImage("Gamepad2.png");//Clean
  Network_Icon = loadImage("Network2.png"); //Clean
  // Load Shader
  Galaxy = loadShader("Galaxy.glsl");
  Galaxy.set("resolution", size.x, size.y);
  Grid_Generator = loadShader("Grid.glsl");
  Grid_Generator.set("resolution", size.x*2, size.y*2);
  Title_Shader = loadShader("Title.glsl");
  Title_Shader.set("resolution", size.x, size.y);
  Scanline_Generator = loadShader("Scanline.glsl");
  Scanline_Generator.set("resolution", size.x, size.y);
  // Load Svg
  Asteroid = loadShape("Asteroid.svg");
  Asteroid.disableStyle();
  Keyboard = loadShape("Keyboard.svg");
  Keyboard.setFill(color(255)); 
  Keyboard.scale(0.8);
  // Load Sound
  if (Sound) {
    Fire = new SoundFile(this, "Sound/Fire.aiff");
    Fire.amp(0.4);
    th = new SoundFile(this, "Sound/Asteroids_Thrust.wav");
    th.loop(0, 0.2);
    n1 = new WhiteNoise(this);
    n1.amp(0.0);
    n1.play();
    T1 = new SoundFile(this, "Sound/Track_1.flac");
    T2 = new SoundFile(this, "Sound/Track_2.flac");
    T3 = new SoundFile(this, "Sound/Track_3.flac");
    T4 = new SoundFile(this, "Sound/Track_4.flac");
    switch(int(random(0, 4))) {
    case 0: 
      T1.loop();
      break;
    case 1: 
      T2.loop();
      break;
    case 2: 
      T3.loop();
      break;
    case 3: 
      T4.loop();
      break;
    }
  }
  //  Info_Movie = new Movie(this, "transit.mov");
  // Info_Movie.loop();
  Load_Done = true;
}
void setup() {
  hint(DISABLE_DEPTH_MASK);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  frameRate(62);
  textMode(SHAPE);
  fullScreen(P3D);
  //size(800, 600, P3D);
  Scale.x = width / size.x;
  Scale.y = height / size.y;
  ow = width;
  oh = height;
  pg = createGraphics(int(size.x/4), int(size.y/4), P2D);
  out = createGraphics(int(width), int(height), P3D);
  hud = createGraphics(int(size.x), int(size.y), P2D);
  Playfield = createGraphics(int(size.x*2), int(size.y*2), P3D);
  thread("Load");
  Menu_Ani = 0;
  In_Net_Game = In_Local_Game = Is_Server =  Load_Done =  Paused = bt =  init =  state = false;
}

void draw() {
  //GFX ReSize
  Scale.x = width/size.x;
  Scale.y = height/size.y;
  if (ow != width || oh != height) {
    ow = width;
    oh = height;
    pg = createGraphics(int(size.x/4), int(size.y/4), P2D);
    out = createGraphics(int(width), int(height), P3D);
    hud = createGraphics(int(size.x), int(size.y), P2D);
    Playfield = createGraphics(int(size.x*2), int(size.y*2), P3D);
  }
  //CHK Game state
  if (!Load_Done) Intro();
  else { 
    if (Reset_Game) End_Game();
    if (!In_Local_Game && !Is_Server) Title_Screen();
    if (In_Local_Game) Game_Loop();
  }
}
void keyPressed() {
  Keys.append(str(keyCode));
  if (keyCode==80 && Paused&&In_Local_Game) Paused=false;
  else if (keyCode==80 && !Paused&&In_Local_Game) Paused=true;
}
void keyReleased() {
  Keys.removeValue(str(keyCode));
}

void Get_Key() {//Int Key Acts
  if (In_Local_Game && !Paused) {
    if (Keys.hasValue("65")) {
      Players.get(0).Apply_Force(-4);
    } else if (Keys.hasValue("68")) {
      Players.get(0).Apply_Force(4);
    } else {
      Players.get(0).Apply_Force(Players.get(0).RDrag());
    }
    Players.get(0).m=Keys.hasValue("87");
    if (Keys.hasValue("87"))Players.get(0).mv();
    if (Keys.hasValue("18")&&frameCount-Players.get(0).LFF>6) {
      Projectiles.add(new Projectile(Players.get(0).Location, Players.get(0).Velocity, Players.get(0).Rotation, 0));
      if (Sound)Fire.play();
      Players.get(0).LFF = frameCount;
    }
    if (Game_Mode==1) {
      if (Keys.hasValue("74")) {
        Players.get(1).Apply_Force(-4);
      } else if (Keys.hasValue("76")) {
        Players.get(1).Apply_Force(4);
      } else {
        Players.get(1).Apply_Force(Players.get(1).RDrag());
      }
      Players.get(1).m=Keys.hasValue("73");
      if (Keys.hasValue("73"))Players.get(1).mv();
      if (Keys.hasValue("19")&&frameCount-Players.get(1).LFF>6) {
        Projectiles.add(new Projectile(Players.get(1).Location, Players.get(1).Velocity, Players.get(1).Rotation, 1));
        if (Sound)Fire.play();
        Players.get(1).LFF = frameCount;
      }
    }
  }
}

class Physics_Object {// Game Phy
  PVector Location, Velocity, Acceleration, Dir;
  float Angle, RVelocity, RAcceleration;
  float Size, Rot_Dir, Rotation, Mass, RMass, Drag_Coefficient, RDrag_Coefficient, Wrap_Offset;
  boolean Is_Game_Obj=false;
  void Apply_Force(PVector Force) {
    if (!Is_Game_Obj || !Paused) 
      Acceleration.add(PVector.div(Force, Mass));
  }
  void Apply_Force(float Force) {
    if (!Is_Game_Obj || !Paused) 
      RAcceleration+=(Force/RMass);
  }
  PVector Drag() {
    float speed = Velocity.mag(), Drag_Magnitude = Drag_Coefficient * speed * speed;
    PVector drag = Velocity.copy();
    drag.mult(-1);
    drag.setMag(Drag_Magnitude);
    return(drag);
  }
  float RDrag() {
    float speed = abs(RVelocity), Drag_Magnitude = RDrag_Coefficient * speed * speed;
    float drag = RVelocity;
    drag*=(-1);
    drag*=Drag_Magnitude;
    return(drag);
  }
  void Move() {
    if (!Is_Game_Obj || !Paused) {
      Rotation += Rot_Dir;
      RVelocity += RAcceleration;
      Angle+=RVelocity;
      RAcceleration=0;
      if (Angle > 360) Angle -= 360;
      if (Angle < 0) Angle += 360;
      Velocity.add(Acceleration);
      Location.add(Velocity);
      Acceleration.mult(0);
      if (Wrap_Offset>=0) {
        if (Location.x <- Wrap_Offset) Location.x += (size.x*2+Wrap_Offset);
        if (Location.x > size.x*2+Wrap_Offset) Location.x -= (size.x*2+Wrap_Offset);
        if (Location.y <- Wrap_Offset) Location.y += (size.y*2+Wrap_Offset);
        if (Location.y > size.y*2+Wrap_Offset) Location.y -= (size.y*2+Wrap_Offset);
      }
    }
  }
}