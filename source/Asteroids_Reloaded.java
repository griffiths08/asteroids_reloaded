import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.core.PApplet; 
import processing.net.*; 
import processing.sound.*; 
import processing.video.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Asteroids_Reloaded extends PApplet {





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
public void movieEvent(Movie m) {
  m.read();
}
public void Load() { //Load Game assts
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
  Keyboard.scale(0.8f);
  // Load Sound
  if (Sound) {
    Fire = new SoundFile(this, "Sound/Fire.aiff");
    Fire.amp(0.4f);
    th = new SoundFile(this, "Sound/Asteroids_Thrust.wav");
    th.loop(0, 0.2f);
    n1 = new WhiteNoise(this);
    n1.amp(0.0f);
    n1.play();
    T1 = new SoundFile(this, "Sound/Track_1.flac");
    T2 = new SoundFile(this, "Sound/Track_2.flac");
    T3 = new SoundFile(this, "Sound/Track_3.flac");
    T4 = new SoundFile(this, "Sound/Track_4.flac");
    switch(PApplet.parseInt(random(0, 4))) {
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
public void setup() {
  hint(DISABLE_DEPTH_MASK);
  textureMode(NORMAL);
  textureWrap(REPEAT);
  frameRate(62);
  textMode(SHAPE);
  
  //size(800, 600, P3D);
  Scale.x = width / size.x;
  Scale.y = height / size.y;
  ow = width;
  oh = height;
  pg = createGraphics(PApplet.parseInt(size.x/4), PApplet.parseInt(size.y/4), P2D);
  out = createGraphics(PApplet.parseInt(width), PApplet.parseInt(height), P3D);
  hud = createGraphics(PApplet.parseInt(size.x), PApplet.parseInt(size.y), P2D);
  Playfield = createGraphics(PApplet.parseInt(size.x*2), PApplet.parseInt(size.y*2), P3D);
  thread("Load");
  Menu_Ani = 0;
  In_Net_Game = In_Local_Game = Is_Server =  Load_Done =  Paused = bt =  init =  state = false;
}

public void draw() {
  //GFX ReSize
  Scale.x = width/size.x;
  Scale.y = height/size.y;
  if (ow != width || oh != height) {
    ow = width;
    oh = height;
    pg = createGraphics(PApplet.parseInt(size.x/4), PApplet.parseInt(size.y/4), P2D);
    out = createGraphics(PApplet.parseInt(width), PApplet.parseInt(height), P3D);
    hud = createGraphics(PApplet.parseInt(size.x), PApplet.parseInt(size.y), P2D);
    Playfield = createGraphics(PApplet.parseInt(size.x*2), PApplet.parseInt(size.y*2), P3D);
  }
  //CHK Game state
  if (!Load_Done) Intro();
  else { 
    if (Reset_Game) End_Game();
    if (!In_Local_Game && !Is_Server) Title_Screen();
    if (In_Local_Game) Game_Loop();
  }
}
public void keyPressed() {
  Keys.append(str(keyCode));
  if (keyCode==80 && Paused&&In_Local_Game) Paused=false;
  else if (keyCode==80 && !Paused&&In_Local_Game) Paused=true;
}
public void keyReleased() {
  Keys.removeValue(str(keyCode));
}

public void Get_Key() {//Int Key Acts
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
  public void Apply_Force(PVector Force) {
    if (!Is_Game_Obj || !Paused) 
      Acceleration.add(PVector.div(Force, Mass));
  }
  public void Apply_Force(float Force) {
    if (!Is_Game_Obj || !Paused) 
      RAcceleration+=(Force/RMass);
  }
  public PVector Drag() {
    float speed = Velocity.mag(), Drag_Magnitude = Drag_Coefficient * speed * speed;
    PVector drag = Velocity.copy();
    drag.mult(-1);
    drag.setMag(Drag_Magnitude);
    return(drag);
  }
  public float RDrag() {
    float speed = abs(RVelocity), Drag_Magnitude = RDrag_Coefficient * speed * speed;
    float drag = RVelocity;
    drag*=(-1);
    drag*=Drag_Magnitude;
    return(drag);
  }
  public void Move() {
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
class Asteroid extends Physics_Object {
  int ID = 0;
  int co = 0xffFFCC00;
  Asteroid(PVector L, float S) {
    Is_Game_Obj = true;
    rc();
    Rotation = random(0, +360);
    Rot_Dir = random(-1.145916f, +1.145916f);
    Location = L.copy();
    Size = S;
    Velocity = PVector.random2D().mult(0.4f);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1f;
    Wrap_Offset = 100;
  }
  Asteroid(PVector L) {
    Is_Game_Obj = true;
    rc();
    Rotation = random(0, +360);
    Rot_Dir = random(-1.145916f, +1.145916f);
    Location = L.copy();
    Size = PApplet.parseInt(random(1, 4))*0.1f;
    Velocity = PVector.random2D().mult(0.4f);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1f;
    Wrap_Offset = 100;
  }
  public void Update() {
    if (Velocity.mag() > 0.6f) Apply_Force(Drag());
    Move();
    Render();
  }
  public void Render() {
    Playfield.fill(255, 60);
    Playfield.pushMatrix();
    Playfield.strokeWeight(2);
    Playfield.translate(Location.x, Location.y);
    Playfield.rotate(radians(Rotation));
    Playfield.scale(Size);
    Playfield.stroke(co);
    Playfield.shape(Asteroid, -169.5f, -171);
    Playfield.rotate(radians(-Rotation));
    Playfield.translate(-Location.x, -Location.y);
    Playfield.popMatrix();
    Playfield.fill(255);
    Playfield.strokeWeight(1);
  }
  public void rc() {
    switch(PApplet.parseInt(random(0, 3))) {
    case 0: 
      co = 0xff0FFFF1;
      break;
    case 1: 
      co = 0xffFF76F6;
      break;
    case 2: 
      co = 0xffFAE923;
      break;
    }
  }
}
public void Draw_Background() {//Background
  background(0);
  if (frameCount % 2 == 0) {
    pg.beginDraw();
    Galaxy.set("time", millis() / 1000.0f); // Set Shader Time
    pg.shader(Galaxy); // Set Shader
    pg.rect(0, 0, size.x, size.y); // Draw Backround
    pg.resetShader(); // Reset Shader
    pg.endDraw();
    bt = true;
  }
}
public void Render_Playfield() {
  Playfield.beginDraw();
  Playfield.background(0, 0);
  Playfield.shader(Grid_Generator); // Set Shader
  Playfield.rect(0, 0, size.x*2, size.y*2);
  Playfield.resetShader();
  Update_Particle_Effects();
  Update_Players();
  Update_Asteroids();
  Update_Projectiles();
  Playfield.endDraw();
}
public void Render_Screen() {
  out.beginDraw();
  out.background(0, 0);
  out.fill(0, 0);
  out.translate(Players.get(0).Location.x, Players.get(0).Location.y);
  out.translate(-Players.get(0).Location.x, -Players.get(0).Location.y);
  out.translate(size.x, size.y, -200);
  if (Game_Mode==1) {
  } else {
    out.rotateY(radians(map(Players.get(0).Location.x, 0, size.x*2, -10, 10)));
    out.rotateX(radians(map(Players.get(0).Location.y, 0, size.y*2, 10, -10)));
  }
  out.rect(-size.x, -size.y, size.x*2, size.y*2);
  out.image(Playfield, -size.x, -size.y, size.x*2, size.y*2);
  out.fill(0, 150);
  if (Game_Mode==0) {
    out.rect(-size.x-(size.x*0.05f*sin(radians(bounds)))-(size.x*0.05f*2), -size.y, size.x*0.05f, size.y*2);
    out.rect(size.x+(size.x*0.05f)*sin(radians(bounds))+(size.x*0.05f), -size.y, size.x*0.05f, size.y*2);
    out.rect(-size.x, -size.y-(size.y*0.05f*sin(radians(bounds)))-(size.y*0.05f*2), size.x*2, size.y*0.05f);
    out.rect(-size.x, size.y+(size.y*0.05f*sin(radians(bounds)))+(size.y*0.05f), size.x*2, size.y*0.05f);
    out.rect(-size.x-(size.x*0.05f*sin(radians(bounds)))-(size.x*0.05f*2), -size.y-(size.y*0.05f*sin(radians(bounds)))-(size.y*0.05f*2), size.x*0.05f, size.y*0.05f);
    out.rect(size.x+(size.x*0.05f)*sin(radians(bounds))+(size.x*0.05f), -size.y-(size.y*0.05f*sin(radians(bounds)))-(size.y*0.05f*2), size.x*0.05f, size.y*0.05f);
    out.rect(-size.x-(size.x*0.05f*sin(radians(bounds)))-(size.x*0.05f*2), size.y+(size.y*0.05f*sin(radians(bounds)))+(size.y*0.05f), size.x*0.05f, size.y*0.05f);
    out.rect(size.x+(size.x*0.05f)*sin(radians(bounds))+(size.x*0.05f), size.y+(size.y*0.05f*sin(radians(bounds)))+(size.y*0.05f), size.x*0.05f, size.y*0.05f);
  }
  bounds += 4;
  out.camera();
  Draw_GUI();

  if (Game_Mode==1) {
    PVector fp= PVector.lerp(Players.get(0).Location, Players.get(1).Location, 0.5f);
    out.camera(fp.x, fp.y, ((size.y+Players.get(0).Location.dist(Players.get(1).Location)*0.7f)/2.0f) / tan(PI*30.0f / 180.0f)-cam, fp.x, fp.y, -50, 0, 1, 0);
  } else
    out.camera(Players.get(0).Location.x, Players.get(0).Location.y, (size.y/2.0f) / tan(PI*30.0f / 180.0f)-cam, Players.get(0).Location.x, Players.get(0).Location.y, -50, 0, 1, 0);
  out.endDraw();
}
public void Render_Output() {
  fill(0, 0);
  if (bt)image(pg, 0, 0, size.x*Scale.x, size.y*Scale.y);
  Scanline_Generator.set("tex0", out);
  shader(Scanline_Generator);
  rect(0, 0, size.x*Scale.x, size.y*Scale.y);
  resetShader();
  Scanline_Generator.set("tex0", hud);
  shader(Scanline_Generator);
  rect(0, 0, size.x*Scale.x, size.y*Scale.y);
  resetShader();
}
public void Draw_GUI() {
  hud.beginDraw();
  hud.background(0, 0);
  hud.fill(0, 200);
  hud.stroke(255, 0);
  hud.rect(0, 0, size.x, 100);
  hud.fill(0, 100);
  hud.stroke(255, 200);
  hud.fill(0, 200);
  if (Game_Mode!=1) hud.rect(50, 20, 200, 20);
  if (Game_Mode==1) hud.rect(50, 20, Players.get(0).Health, 20);
  if (Game_Mode==1) hud.rect(50, 60, Players.get(1).Health, 20);
  hud.stroke(255, 255);
  hud.fill(0xff0FFFF1, 200);
  hud.rect(50, 20, Players.get(0).Health, 6);
  hud.fill(0xffFF76F6, 200);
  hud.rect(50, 20+6, Players.get(0).Health, 6);
  hud.fill(0xffFAE923, 200);
  hud.rect(50, 20+12, Players.get(0).Health, 6);
  if (Game_Mode==1) {
    hud.fill(0xff0FFFF1, 200);
    hud.rect(50, 60, Players.get(1).Health, 6);
    hud.fill(0xffFF76F6, 200);
    hud.rect(50, 60+6, Players.get(1).Health, 6);
    hud.fill(0xffFAE923, 200);
    hud.rect(50, 60+12, Players.get(1).Health, 6);
  }
  hud.textFont(Font1, 40);
  hud.fill(255);
  hud.stroke(255);
  if (Game_Mode!=1) hud.text(str(PApplet.parseInt(Players.get(0).Health/2))+'%', 20+Players.get(0).Health+5, 75);
  if (Game_Mode==1) hud.text(str(PApplet.parseInt(Players.get(0).Health/2))+'%', 60+Players.get(0).Health+5, 45);
  if (Game_Mode==1) hud.text(str(PApplet.parseInt(Players.get(1).Health/2))+'%', 60+Players.get(1).Health+5, 85);
  hud.textFont(Font1);
  hud.text("P1 :"+str(Players.get(0).Score), 400, 50);
  if (Game_Mode==1)hud.text("P2 :"+str(Players.get(1).Score), 400, 90);
  if (Game_Mode==2)hud.text("Time :"+str(PApplet.parseInt((Time-frameCount)/.6f)), 400, 90);
  hud.text("High Score", 720, 50);
  hud.text(str(High_Score), 720, 90);
  if (Paused&&Dialog_Boxs.size()==0) new Dialog(new PVector(size.x/2, size.y/2), 5).Update();
  for (int i = Dialog_Boxs.size()-1; i >= 0; i--) { 
    Dialog_Boxs.get(i).Update();
    if (Dialog_Boxs.get(i).dis) Dialog_Boxs.remove(i);
  }
  hud.endDraw();
}
public void Intro() {// Make Logo
  if (!init)Init_Intro();
  Intro_loop();
}
public void Init_Intro() {
  init=true;
  for (int angle = 0; angle < 360; angle += 9) {
    PVector v = PVector.fromAngle(radians(angle-135));
    v.mult(100);
    circle.add(v);
    morph.add(new PVector());
  }
  for (int x = -50; x < 50; x += 10) square.add(new PVector(x, -50)); // Top of square
  for (int y = -50; y < 50; y += 10) square.add(new PVector(50, y)); // Right side
  for (int x = 50; x > -50; x -= 10) square.add(new PVector(x, 50)); // Bottom
  for (int y = 50; y > -50; y -= 10) square.add(new PVector(-50, y)); // Left side
}

public void Intro_loop() {
  background(51);
  float totalDistance = 0;
  for (int i = 0; i < circle.size(); i++) {
    PVector v1;
    if (state)  v1 = circle.get(i);
    else v1 = square.get(i);
    PVector v2 = morph.get(i);
    v2.lerp(v1, 0.1f);
    totalDistance += PVector.dist(v1, v2);
  }
  translate(width/2, height/2);
  strokeWeight(2);
  scale(2);
  text("By: Fred Griffiths", -20, 0);
  text("Music is CC-COM", -20, 20);
  rotate(ra3);
  ra3+=0.02f;
  beginShape();
  noFill();
  stroke(255);
  for (PVector v : morph) vertex(v.x, v.y);
  endShape(CLOSE);
  stroke(255);
  fill(255);
}
public void Start_Local_Game() {
  switch(Game_Mode) {
  case 0: 
    for (int i = 0; i < 20; i++) Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Players.add(new Player(size.x, size.y));
    break;
  case 1: 
    for (int i = 0; i < 40; i++) Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Dialog_Boxs.add(new Dialog(new PVector(400, 600), 1));
    Players.add(new Player(size.x, size.y));
    Players.add(new Player(size.x, size.y));
    break;
  case 2: 
    for (int i = 0; i < 30; i++) Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Players.add(new Player(size.x, size.y));
    Time = frameCount + (60*60);
    break;
  }
  Paused = In_Local_Game = true;
}

public void End_Game() {
  In_Local_Game = Paused = false;
  Game_Mode = 0;
  Asteroids.clear();
  Players.clear();
  Projectiles.clear();
  Particle_Systems.clear();
  ex.clear();
  Dialog_Boxs.clear();
  Keys.clear();
  Reset_Game = false;
}

public void Game_Loop() {
  Get_Key();
  Draw_Background();
  Scanline_Generator.set("time", millis() / 1000.0f); // Set Shader Time
  Grid_Generator.set("time", millis() / 1000.0f); // Set Shader Time
  Render_Playfield();
  Render_Screen();
  Render_Output();
  col();
  if (Asteroids.size()<20 && Game_Mode==0) {
    Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
  }
  if (Asteroids.size()<20 && Game_Mode==1) {
    Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
  }
  if (Asteroids.size()<30 && Game_Mode==2) {
    Asteroids.add(new Asteroid(new PVector(0, 0), PApplet.parseInt(4)*0.1f));
  }
  if (Game_Mode==0 && Players.get(0).HT<=0) {
    Paused=true;
    if (Dialog_Boxs.size()==0) Dialog_Boxs.add(new Dialog(new PVector(size.x/2, size.y/2), 2));
  } else if (Game_Mode==1 && (Players.get(0).HT<=0||Players.get(1).HT<=0)) {
    Paused=true;
    if (Dialog_Boxs.size()==0) Dialog_Boxs.add(new Dialog(new PVector(size.x/2, size.y/2), 3));
  } else if (Game_Mode==2 && (Players.get(0).HT<=0||Time==frameCount)) {
    Paused=true;
    if (Dialog_Boxs.size()==0) Dialog_Boxs.add(new Dialog(new PVector(size.x/2, size.y/2), 4));
  } 
  if (Game_Mode==2)  if (Paused) Time++;
}
public void col() {
  for (Asteroid a : Asteroids) {
    float AL = a.Location.x-(339*a.Size) / 2, AR = a.Location.x+(339*a.Size)/2, AU = a.Location.y - (342*a.Size) / 2, AD = a.Location.y + (342*a.Size) / 2;
    for (Asteroid b : Asteroids) {
      boolean m = false, m2 = false;
      float BL = b.Location.x - (339*b.Size) / 2, BR = b.Location.x + (339*b.Size) / 2, BU = b.Location.y - (342*b.Size) / 2, BD = b.Location.y + (342*b.Size) / 2;
      if (BL>AL&&BL<AR)m = true;
      if (BR>AL&&BR<AR)m = true;
      if (BU>AU&&BU<AD)m2 = true;
      if (BD>AU&&BD<AD)m2 = true;
      if (m&&m2) {
        a.Apply_Force(a.Location.copy().sub(b.Location).normalize().mult(0.2f));
        b.Apply_Force(b.Location.copy().sub(a.Location).normalize().mult(0.2f));
      }
    }
  }
  for (int i = Projectiles.size()-1; i >= 0; i--) { 
    Projectile p = Projectiles.get(i);
    for (int b = Asteroids.size()-1; b >= 0; b--) { 
      boolean m = false, m2 = false;
      float BL = Asteroids.get(b).Location.x - (339*Asteroids.get(b).Size) / 2, BR = Asteroids.get(b).Location.x + (339*Asteroids.get(b).Size) / 2, BU = Asteroids.get(b).Location.y - (342*Asteroids.get(b).Size) / 2, BD = Asteroids.get(b).Location.y + (342*Asteroids.get(b).Size) / 2;
      if (p.Location.x>BL&&p.Location.x<BR)m = true;
      if (p.Location.y>BU&&p.Location.y<BD)m2 = true;
      if (m&&m2) {
        PFX1(Asteroids.get(b).Location);
        if (PApplet.parseInt(Asteroids.get(b).Size*10)>1) {
          if ((PApplet.parseInt(Asteroids.get(b).Size*10) == 3)) Add_Score(20, Projectiles.get(i).ID);
          if ((PApplet.parseInt(Asteroids.get(b).Size*10) == 2)) Add_Score(60, Projectiles.get(i).ID);
          Asteroids.get(b).Size -= 0.1f;
          Asteroids.get(b).Dir = PVector.random2D().mult(random(1, 2));
          Asteroids.add(new Asteroid(Asteroids.get(b).Location, Asteroids.get(b).Size));
        } else {
          Add_Score(200, Projectiles.get(i).ID);
          EX1(Asteroids.get(b).Location, 200);
          Asteroids.remove(b);
        }
        EX1(Projectiles.get(i).Location, 20);
        Projectiles.remove(i);
        break;
      }
    }
  }
  for (int q = Players.size()-1; q >= 0; q--) { 
    Player S = Players.get(q);
    boolean h2 = false;
    float BL = S.Location.x - (40) / 2, BR = S.Location.x + (40) / 2, BU = S.Location.y - (40) / 2, BD = S.Location.y + (40) / 2;
    for (int i = Projectiles.size()-1; i >= 0; i--) { 
      Projectile p = Projectiles.get(i);
      boolean m = false, m2 = false;
      if (p.Location.x>BL&&p.Location.x<BR)m = true;
      if (p.Location.y>BU&&p.Location.y<BD)m2 = true;
      if (m&&m2) {
        h2 = true;
        if (!S.h) {
          if (p.ID!=q)
          {
            S.h = true;
            S.HT -= 20;
            PFX1(S.Location);
            S.Apply_Force(S.Location.copy().sub(p.Location).normalize().mult(10));
          }
        }
      }
    }
    for (Asteroid a : Asteroids) {
      float AL = a.Location.x - (339*a.Size) / 2, AR = a.Location.x + (339*a.Size) / 2, AU = a.Location.y - (342*a.Size) / 2, AD = a.Location.y + (342*a.Size) / 2;
      boolean m = false, m2 = false;
      if (BL>AL&&BL<AR)m = true;
      if (BR>AL&&BR<AR)m = true;
      if (BU>AU&&BU<AD)m2 = true;
      if (BD>AU&&BD<AD)m2 = true;
      if (m&&m2) {
        h2 = true;
        PFX1(S.Location);
        S.Apply_Force(S.Location.copy().sub(a.Location).normalize().mult(1));
        if (!S.h) {
          S.h = true;
          S.HT -= 10;
        }
      }
    }
    if (!h2) S.h = false;
  }
}
public void Update_Players() {
  for (Player S : Players) { 
    S.Update();
    if (S.Health > S.HT) PFX2(new PVector(10+S.Health, 20));
    if (S.Health > S.HT) S.Health -= 1;
    if (S.ST - S.Score >= 100) S.Score += 50;
    if (S.ST - S.Score >= 40) S.Score += 20;
    if (S.ST - S.Score >= 1) S.Score += 1;
    if (S.Score > High_Score) High_Score = S.Score;
  }
}
public void Update_Asteroids() {
  for (Asteroid a : Asteroids) a.Update();
}
public void Update_Projectiles() {
  for (int i = Projectiles.size()-1; i >= 0; i--) { 
    Projectile p = Projectiles.get(i);
    p.Update();
    if (frameCount-p.Timeout>120)Projectiles.remove(i);
  }
}
public void Add_Score(float s, int p) {
  Players.get(p).ST += s;
}
class Dialog extends Physics_Object {
  boolean dis = false;
  int ID = 0, Type = 0, Delay = 200;
  Dialog(PVector L, int T) {
    Size = 1;
    Rotation = 0;
    Rot_Dir = 0;
    Location = L.copy();
    Type = T;
    Velocity = PVector.random2D().mult(0);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1f;
    Wrap_Offset = -1;
  }
  public void Update() {
    Move();
    if (Type == 0 && Location.x>=size.x+400) {
      dis = true;
      Paused = false;
    }
    if (Type == 1 && Location.x<-200)dis=true;
    //if (Delay==0) {
    if (Keys.hasValue("10")) {
      if (Type==0) Velocity = PVector.fromAngle(radians(0)).mult(10);
      if (Type==1) Velocity = PVector.fromAngle(radians(180)).mult(10);
      if (Type==0)  Rot_Dir =  +1.145916f;
      if (Type==1)  Rot_Dir =  -1.145916f;
      // Delay--;
      if (Type==2 || Type==3 || Type==4) Reset_Game=true;
    } //else if (Delay>0) Delay--;
    Render();
  }
  public void Render() {
    hud.pushMatrix();
    hud.strokeWeight(1);
    hud.translate(Location.x, Location.y);
    hud.rotate(radians(Rotation));
    hud.scale(Size);
    switch(PApplet.parseInt(random(0, 3))) {
    case 0: 
      hud.stroke(0xff0FFFF1);
      break;
    case 1: 
      hud.stroke(0xffFF76F6);
      break;
    case 2: 
      hud.stroke(0xffFAE923);
      break;
    }
    switch(Type) {
    case 0: 
      hud.fill(0xffFF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(0xff0FFFF1, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFF76F6, 255);
      hud.text("Player 1", -250, -100);
      hud.text("A&D - Rotate", -250, -50);
      hud.text("W - Thrster", -250, -0);
      hud.text("Left Alt - Fire", -250, 50);
      hud.text("P - Pause", -250, 100);
      hud.text("Press Enter", -250, 150);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 1: 
      hud.fill(0xffFF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(0xff0FFFF1, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFF76F6, 255);
      hud.text("Player 2", -250, -100);
      hud.text("J&L - Rotate", -250, -50);
      hud.text("I - Thrster", -250, -0);
      hud.text("Right Alt - Fire", -250, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 2: 
      hud.fill(0xffFF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(0xffFAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFF76F6, 255);
      hud.text("Game Over", -260, -100);
      hud.text("Classic Mode", -260, -50);
      hud.text("Score : "+str(Players.get(0).Score), -260, -0);
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 3: 
      hud.fill(0xffFF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(0xffFAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFF76F6, 255);
      if (Players.get(0).Score>Players.get(1).Score) {
        hud.text("Player 1 Won!", -260, -100);
        hud.text("P1 : "+str(Players.get(0).Score), -260, -50);
        hud.text("P2 : "+str(Players.get(1).Score), -260, -0);
      } else {
        hud.text("Player 2 Won!", -260, -100);
        hud.text("P1 : "+str(Players.get(0).Score), -260, -0);
        hud.text("P2 : "+str(Players.get(1).Score), -260, -50);
      }
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 4: 
      hud.fill(0xffFF76F6, 200);
      hud.rect(-260+random(0, 10), -140, 600, 300);
      hud.fill(0xffFAE923, 255);
      hud.rect(-300, -150, 600, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFF76F6, 255);
      hud.text("Game Over", -260, -100);
      hud.text("Time Mode", -260, -50);
      hud.text("Score : "+str(Players.get(0).Score), -260, -0);
      hud.text("Press Enter", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    case 5: 
      hud.fill(0xffFAE923, 200);
      hud.rect(-260+random(0, 10), -140, 640, 300);
      hud.fill(0xffFF76F6, 255);
      hud.rect(-300, -150, 640, 300);
      hud.textFont(Font1, 60);
      hud.fill(0xffFAE923, 255);
      hud.text("Paused", -260, -100);
      hud.text("Rotate : P1-AD P2-JL", -260, -50);
      hud.text("Move : P1-W P2-I", -260, -0);
      hud.text("Fire : L&R Alt Keys", -260, 50);
      hud.rotate(radians(-Rotation));
      hud.translate(-Location.x, -Location.y);
      hud.popMatrix();
      hud.fill(255);
      hud.strokeWeight(1);
      break;
    }
  }
}
public void Title_Screen() {
  if (mold!=mousePressed) {
    ch = mousePressed;
    mold = mousePressed;
  } else
    ch = false;
  scale(Scale.x, Scale.y);
  //Menu 0=start,1= rot,2=shoot,3=movest,4=move,5=move,6=main,7=local,8=network,9=Mode
  if (Menu_Ani==0) {
    L1.x=100;
    L1.y=300;
    L2.x=160;
    L2.y=400;
    pbangle=360;
    ani1=415;
    ani2=500;
  }
  Title_Shader.set("time", millis() / 2000.0f); // Set Shader Time
  stroke(255);
  fill(0, 255); // Set Fill Color To White
  shader(Title_Shader); // Set Shader
  rect(0, 0, size.x, size.y); // Draw Backround
  resetShader(); // Reset Shader
  noStroke();
  if (Menu_Ani==1) pbangle-=2; 
  if (Menu_Ani==1 && pbangle<270)Menu_Ani =2;
  if (Menu_Ani==2) {
    fill(255, 60);
    rect(495, ani1, 10, 40);
    ani1 -= 6;
  }
  if (Menu_Ani==2&&ani1<200) Menu_Ani=3;
  if (Menu_Ani==3) {
    L1_Dir=PVector.random2D().mult(20);
    L2_Dir=PVector.random2D().mult(20);
    Menu_Ani = 4;
  }
  if (Menu_Ani==4) {
    L1.add(L1_Dir);
    L2.add(L2_Dir);
    if (((L1.x<-1400||L1.x> size.x+400)||(L1.y<-400||L1.y> size.y+400))&&((L2.x<-1400||L2.x> size.x+400)||(L2.y<-400||L2.y> size.y+400)))Menu_Ani = 5;
  }
  if (Menu_Ani==5)ani2-=8;
  if (Menu_Ani==5 && ani2<-100)Menu_Ani=6;
  if (Menu_Ani<6) {
    noStroke();
    fill(255, 80);
    textFont(Logo_Font); // Set font
    scale(0.75f);
    float a = -random(5, 8);
    shearX(radians(a));
    text("Asteroids", L1.x, L1.y); // Draw Title
    shearX(radians(-2*a));
    text("Reloaded", L2.x, L2.y); // Draw Title
    shearX(radians(a));
    scale(1.333333333f);
    translate(500, ani2);
    fill(255, 60);
    pushMatrix();
    rotate(radians(sin(radians((pbangle)))*90));
    scale(8);
    triangle(-10, -10, -10, 10, 10, 0);
    scale(0.08f);
    if (Is_Button_Selected(415, 415, 165, 165)&&Menu_Ani ==0) {
      if (Cl())Menu_Ani=1;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 80);// Set Fill Color
    }
    scale(6);
    triangle(-10, -10, -10, 10, 10, 0);
    popMatrix();
    translate(-500, -ani2);
    fill(255, 60); // Set Fill Color To White
  }
  if (Menu_Ani>5) stroke(255);
  if (Menu_Ani ==6) {
    textFont(Menu_Font);
    if (Is_Button_Selected(100, 100, 100, 100)) {
      if (Cl())Menu_Ani=9;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100, 100, 100);
    image(GamePad_Icon, 112.5f, 112.5f, 75, 75);
    text("Local Game", 210, 155); // Draw Button Text
    if (Is_Button_Selected(100, 220, 100, 100)) {
      if (Cl())Menu_Ani=8;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60); // Set Fill Color
    }
    rect(100, 220, 100, 100);
    image(Network_Icon, 112.5f, 232.5f, 75, 75);
    text("Conect to Game Network", 210, 275); // Draw Button Text
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=0;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5f, 612.5f, 75, 75);
    text("Back", 210, 655); // Draw Button Text
  }
  if (Menu_Ani ==7) {
    //-----------------------------------
    if (Is_Button_Selected(200, 400, 100, 100)) {
      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("Q", 215, 472); // Draw Text
    textFont(Menu_Font);
    //-----------------------------------
    // shape(Keyboard, 10, 100);
    fill(255, 60);
    text("Players:", 120, 255); // Draw Text
    if (Is_Button_Selected(200, 200, 100, 100)) {
      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("1", 215, 272); // Draw Text
    textFont(Menu_Font);
    if (Is_Button_Selected(300, 200, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    textFont(Menu_Font2);
    text("2", 315, 272); // Draw Text
    textFont(Menu_Font);
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5f, 612.5f, 75, 75);
    text("Back", 210, 655); // Draw Button Text
  }
  if (Menu_Ani ==8) {
    if (Is_Button_Selected(100, 600, 100, 100)) {
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600, 100, 100);
    image(Back_Icon, 112.5f, 612.5f, 75, 75);
    text("Back", 210, 655); // Draw Button Text
    if (Is_Button_Selected(260, 600, 100, 100)) {
      if (Cl()) Is_Server =true;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(260, 600, 100, 100);
    image(Back_Icon, 112.5f, 612.5f, 75, 75);
    text("Start Server", 380, 655); // Draw Button Text
    fill(255, 60);
    rect(150, 100, 400, 400);
    if (Is_Button_Selected(360, 600, 100, 100)) {
      if (Cl()) In_Net_Game =true;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(360, 600, 100, 100);
    image(Back_Icon, 312.5f, 612.5f, 75, 75);
    text("Start Client", 480, 655); // Draw Button Text
    fill(255, 60);
  }
  if (Menu_Ani == 9) {
    boolean se=false;
    //-------Back
    if (Is_Button_Selected(100, 600+40, 100, 100)) {
      se = true;
      if (Cl())Menu_Ani=6;
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 600+40, 100, 100);
    image(Back_Icon, 112.5f, 612.5f+40, 75, 75);
    text("Back", 210, 655+40); // Draw Button Text
    //--------------------
    //-------Classic
    if (Is_Button_Selected(100, 100-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Classic Asteroids", 410, 520);


      if (Cl())Start_Local_Game();
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100-20, 100, 100);
    image(Back_Icon, 112.5f, 112.5f-20, 75, 75);
    text("Classic Mode", 210, 155-20); // Draw Button Text
    //--------------------
    //-------VS
    if (Is_Button_Selected(100, 100+140-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Vs. Mode", 410, 520);
      text("2 Player Asteroids; Play against a friend", 410, 540);
      if (Cl()) {
        Game_Mode=1;
        Start_Local_Game();
      }
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100+140-20, 100, 100);
    image(Back_Icon, 112.5f, 112.5f+140-20, 75, 75);
    text("Vs. Mode", 210, 155+140-20); // Draw Button Text
    //--------------------
    //-------Time Challenge
    if (Is_Button_Selected(100, 100+280-20, 100, 100)) {
      se = true;
      stroke(255);
      fill(255);
      text("Time Challenge Mode", 410, 520);
      text("Score as many points as you can in 60 seconds.", 410, 540);
      if (Cl()) {
        Game_Mode=2;
        Start_Local_Game();
      }
      fill(10, 100, 255, 150); // Set Fill Color To Blue
    } else {
      fill(255, 60);// Set Fill Color
    }
    rect(100, 100+280-20, 100, 100);
    image(Back_Icon, 112.5f, 112.5f+280-20, 75, 75);
    text("Time Challenge Mode", 210, 155+280-20); // Draw Button Text
    //--------------------
    //-------Puzzle
    //if (Is_Button_Selected(100, 100+420-20, 100, 100)) {
    // se = true;
    //  stroke(255);
    //  fill(255);
    //  text("Puzzle Mode", 410, 520);
    //  if (Cl())Menu_Ani=6;
    //  fill(10, 100, 255, 150); // Set Fill Color To Blue
    //} else {
    //  fill(255, 60);// Set Fill Color
    //}
    //rect(100, 100+420-20, 100, 100);
    //image(Back_Icon, 112.5, 112.5+420-20, 75, 75);
    //text("Puzzle Mode", 210, 155+420-20); // Draw Button Text
    //--------------------
    if (!se) {
      stroke(255);
      fill(255);
      text("Score:", 410, 520);
      text("20 - Large 50 - Medium 100 - Small", 410, 540);
    }
    //-------Video
    // image(Info_Movie, 400, 80, 400, 400);
    fill(255, 60);// Set Fill Color
    rect(400, 500, 400, 250);
    //--------------------
  }
}
public boolean Cl() {
  if (ch==true) {
    ch=!ch;
    return !ch;
  } else
    return ch;
}
public boolean Is_Button_Selected(int x, int y, int width, int height) {
  x*=Scale.x;
  y*=Scale.y;
  width*=Scale.x;
  height*=Scale.y;
  if (mouseX >= x && mouseX <= x+width && mouseY >= y && mouseY <= y+height) { // Is Mouse Over Button
    return true;
  } else {
    return false;
  }
}
public void Update_Particle_Effects() {
  for (int b = Particle_Systems.size()-1; b >= 0; b--) { 
    Particle_Systems.get(b).Update();
    if (Particle_Systems.get(b).Particles.size() ==0)Particle_Systems.remove(b);
  }
  for (int b = ex.size()-1; b >= 0; b--) { 
    ex.get(b).Update();
    if (ex.get(b).isDead())ex.remove(b);
  }
}
public void PFX1(PVector L) {
  Particle_System P = new Particle_System(L, 0);
  for (int i=0; i<40; i++) P.addParticle();
  Particle_Systems.add(P);
}
public void PFX2(PVector L) {
  Particle_System P = new Particle_System(L, 1);
  for (int i=0; i<10; i++) P.addParticle();
  Particle_Systems.add(P);
}

public void EX1(PVector L, float ts) {
  ex.add(new exp(L, ts));
}
class exp {

  PVector Location;
  float Size, tsize;
  exp(PVector L, float ts) {
    Location =L.copy();
    Size=1;
    tsize=ts;

    if (Sound) n1.amp(0.4f);
  }
  public void Update() {
    // color co = color(int(random(60, 255)), int(random(60, 255)), int(random(60, 255)));
    // stroke(255, 100);
    //shader(FireShader); // Set Shader
    //   rect(0, 0, float(width), float(height)); // Draw Backround
    Playfield.ellipse(Location.x, Location.y, Size, Size);
    int co=0xffFFCC00;
    switch(PApplet.parseInt(random(0, 3))) {
    case 0: 
      co = 0xff0FFFF1;
      break;
    case 1: 
      co = 0xffFF76F6;
      break;
    case 2: 
      co = 0xffFAE923;
      break;
    }
    //resetShader(); // Reset Shader
    Playfield.fill(co, 100);
    Playfield.stroke(255, 100);
    Playfield.strokeWeight(4);
    Playfield.ellipse(Location.x, Location.y, Size, Size);
    Playfield.strokeWeight(1);
    //    fill(co, 100);
    if (!Paused) Size+=16;
  }
  public boolean isDead() {
    if (Size>tsize) {
      if (Sound) n1.amp(0);
      return true;
    } else {
      return false;
    }
  }
}
class Particle_System {
  ArrayList<Particle> Particles;
  PVector Origin;
  int Type;
  Particle_System(PVector Location, int T) {
    Origin = Location.copy();
    Particles = new ArrayList<Particle>();
    Type = T;
  }
  public void addParticle() {
    Particles.add(new Particle(Origin, Type));
  }
  public void addParticle(float Angle) {
    Particle p = new Particle(Origin, Type);
    p.Velocity = PVector.fromAngle(radians(Angle)).mult(10);
    Particles.add(p);
  }
  public void Update() {
    for (int i = Particles.size()-1; i >= 0; i--) {
      Particle P = Particles.get(i);
      if (P.isDead()) Particles.remove(i);
      else P.update();
    }
  }
}

class Particle extends Physics_Object {
  float lifespan, Size, Rot;

  int Type;
  int co=0xffFFCC00;
  Particle(PVector L, int T) {
    Is_Game_Obj= true;
    switch(PApplet.parseInt(random(0, 3))) {
    case 0: 
      co = 0xff0FFFF1;
      break;
    case 1: 
      co = 0xffFF76F6;
      break;
    case 2: 
      co = 0xffFAE923;
      break;
    }
    Type = T;
    Acceleration = new PVector(0, 0);
    if (Type==1) {
      Velocity = new PVector(random(-1, 1), random(1, 2));
    } else if (Type==2) {
      Velocity = new PVector(random(-4, 4), random(-4, 4));
    } else {
      Velocity = new PVector(random(-4, 4), random(-4, 4));
    }
    Location = L.copy();
    if (Type==2)  lifespan = random(40);
    else lifespan = random(100);
    Size = random(4);
    Rot = random(0, 361);
    Rot_Dir = random(-11.4592f*2, 11.4592f*2);
    Wrap_Offset=0;
  }
  public void update() {
    lifespan -= 1.0f;
    Move();
    Render();
  }
  public void Render() {
    stroke(0, 0, 0, 0);
    if (Type==1) {
      translate(Location.x, Location.y);
      rotate(radians(Rot));
      stroke(255, 0, 0, lifespan*2.55f);
      fill(255, 0, 0, lifespan*2.55f);
      rect(-(Size/2), -(Size/2), Size, Size);
      rotate(radians(-Rot));
      translate(-Location.x, -Location.y);
    } else if (Type==2) {
      Playfield.translate(Location.x, Location.y);
      Playfield.rotate(radians(Rot));
      Playfield.stroke(co, lifespan*6.375f);
      Playfield.fill(co, lifespan*6.375f);
      Playfield.rect(-(Size/2), -(Size/2), Size, Size);
      Playfield.rotate(radians(-Rot));
      Playfield.translate(-Location.x, -Location.y);
    } else {
      Playfield.translate(Location.x, Location.y);
      Playfield.rotate(radians(Rot));
      Playfield.stroke(co, lifespan*2.55f);
      Playfield.fill(co, lifespan*2.55f);
      Playfield.rect(-(Size/2), -(Size/2), Size, Size);
      Playfield.rotate(radians(-Rot));
      Playfield.translate(-Location.x, -Location.y);
    }
  }

  public boolean isDead() {
    if (lifespan < 0.0f) {
      return true;
    } else {
      return false;
    }
  }
}
class Player extends Physics_Object {
  String Player_ID;
  boolean h = false, m=false;
  float Health, HT, RAccl=0;
  int Lives, Score, ST, LFF;
  Particle_System PF = new Particle_System(new PVector(0, 0), 2);
  Player(float x, float y) {//Sw to vector
    Is_Game_Obj= true;
    Location = new PVector(x, y);
    Velocity = new PVector(0, 0);
    Acceleration = new PVector(0, 0);
    Mass = 8;
    RMass = 4;
    Rotation = 0;
    Wrap_Offset = 0;
    Drag_Coefficient=0.04f;
    RDrag_Coefficient=0.06f;
    Health = 200;
    HT = 200;
    Score = 0;
    ST=0;
    LFF= frameCount;
  }
  public void Update() {
    // println(Angle, RVelocity);
    if (RVelocity>3)RVelocity=3;
    if (RVelocity<-3)RVelocity=-3;
    if (abs(RVelocity)<1)RVelocity=0;
    PF.Origin=Location.copy();
    Rotation=Angle;
    if (!m) Apply_Force(Drag());
   if (Sound) th.rate(Velocity.mag()*0.5f);
    Move();
    PF.Update();
    Playfield.strokeWeight(2);
    Playfield.translate(Location.x, Location.y);
    Playfield.pushMatrix();
    Playfield.rotate(radians(Rotation));
    Playfield.stroke(255);
    Playfield.line(20, 0, -5, -10);
    Playfield.line(20, 0, -5, 10);
    Playfield.line(-5, -10, -5, 10);
    if (m) {
      for (int i=0; i<10; i++) PF.addParticle(Angle+180+random(-20, 20));
      switch(1) {
      case 0: 
        Playfield.stroke(0xff0FFFF1);
        break;
      case 1: 
        Playfield.stroke(0xffFF76F6);
        break;
      case 2: 
        Playfield.stroke(0xffFAE923);
        break;
      }
      //Playfield.stroke(color(int(random(10, 255)), int(random(10, 255)), int(random(10, 255))));
      Playfield.line(-5, -5, -14, 0);
      Playfield.line(-5, 5, -14, 0);
      Playfield.stroke(255);
    }
    Playfield.rotate(radians(-Rotation));
    Playfield.popMatrix();
    Playfield.translate(-Location.x, -Location.y);
    Playfield.strokeWeight(1);
  }
  public void mv() {
    Apply_Force(PVector.fromAngle(radians(Rotation)).mult(0.4f));
  }
}
class Projectile {
  int ID = 0;
  PVector Location, vol;
  float Rotation, Timeout=frameCount;
  Projectile(PVector L, PVector v, float R, int id) {
    vol = v.copy();
    Location = L.copy(); 
    Rotation = R;
    ID = id;
  }
  public void Render() {
    Playfield.fill(255);
    Playfield.stroke(255);
    Playfield.translate(Location.x, Location.y);
    Playfield.rotate(radians(Rotation));
    Playfield.rect(0, 0, 4, 2);
    Playfield.rotate(radians(-Rotation));
    Playfield.translate(-Location.x, -Location.y);
  }
  public void Update() {
    if (!Paused) {
      Location.add(PVector.fromAngle(radians(Rotation)).mult(20).add(vol));
      if (Location.x<-100) Location.x+=(size.x*2+100);
      if (Location.x> size.x*2+100) Location.x-=(size.x*2+100);
      if (Location.y<-100) Location.y+=(size.y*2+100);
      if (Location.y> size.y*2+100) Location.y-=(size.y*2+100);
    }
    Render();
  }
}
  public void settings() {  fullScreen(P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Asteroids_Reloaded" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
