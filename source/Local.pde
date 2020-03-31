void Start_Local_Game() {
  switch(Game_Mode) {
  case 0: 
    for (int i = 0; i < 20; i++) Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Players.add(new Player(size.x, size.y));
    break;
  case 1: 
    for (int i = 0; i < 40; i++) Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Dialog_Boxs.add(new Dialog(new PVector(400, 600), 1));
    Players.add(new Player(size.x, size.y));
    Players.add(new Player(size.x, size.y));
    break;
  case 2: 
    for (int i = 0; i < 30; i++) Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
    Dialog_Boxs.add(new Dialog(new PVector(400, 200), 0));
    Players.add(new Player(size.x, size.y));
    Time = frameCount + (60*60);
    break;
  }
  Paused = In_Local_Game = true;
}

void End_Game() {
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

void Game_Loop() {
  Get_Key();
  Draw_Background();
  Scanline_Generator.set("time", millis() / 1000.0); // Set Shader Time
  Grid_Generator.set("time", millis() / 1000.0); // Set Shader Time
  Render_Playfield();
  Render_Screen();
  Render_Output();
  col();
  if (Asteroids.size()<20 && Game_Mode==0) {
    Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
  }
  if (Asteroids.size()<20 && Game_Mode==1) {
    Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
  }
  if (Asteroids.size()<30 && Game_Mode==2) {
    Asteroids.add(new Asteroid(new PVector(0, 0), int(4)*0.1));
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
void col() {
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
        a.Apply_Force(a.Location.copy().sub(b.Location).normalize().mult(0.2));
        b.Apply_Force(b.Location.copy().sub(a.Location).normalize().mult(0.2));
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
        if (int(Asteroids.get(b).Size*10)>1) {
          if ((int(Asteroids.get(b).Size*10) == 3)) Add_Score(20, Projectiles.get(i).ID);
          if ((int(Asteroids.get(b).Size*10) == 2)) Add_Score(60, Projectiles.get(i).ID);
          Asteroids.get(b).Size -= 0.1;
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
void Update_Players() {
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
void Update_Asteroids() {
  for (Asteroid a : Asteroids) a.Update();
}
void Update_Projectiles() {
  for (int i = Projectiles.size()-1; i >= 0; i--) { 
    Projectile p = Projectiles.get(i);
    p.Update();
    if (frameCount-p.Timeout>120)Projectiles.remove(i);
  }
}
void Add_Score(float s, int p) {
  Players.get(p).ST += s;
}