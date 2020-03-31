class Asteroid extends Physics_Object {
  int ID = 0;
  color co = #FFCC00;
  Asteroid(PVector L, float S) {
    Is_Game_Obj = true;
    rc();
    Rotation = random(0, +360);
    Rot_Dir = random(-1.145916, +1.145916);
    Location = L.copy();
    Size = S;
    Velocity = PVector.random2D().mult(0.4);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1;
    Wrap_Offset = 100;
  }
  Asteroid(PVector L) {
    Is_Game_Obj = true;
    rc();
    Rotation = random(0, +360);
    Rot_Dir = random(-1.145916, +1.145916);
    Location = L.copy();
    Size = int(random(1, 4))*0.1;
    Velocity = PVector.random2D().mult(0.4);
    Acceleration = new PVector(0, 0);
    Mass = 4;
    Drag_Coefficient = 0.1;
    Wrap_Offset = 100;
  }
  void Update() {
    if (Velocity.mag() > 0.6) Apply_Force(Drag());
    Move();
    Render();
  }
  void Render() {
    Playfield.fill(255, 60);
    Playfield.pushMatrix();
    Playfield.strokeWeight(2);
    Playfield.translate(Location.x, Location.y);
    Playfield.rotate(radians(Rotation));
    Playfield.scale(Size);
    Playfield.stroke(co);
    Playfield.shape(Asteroid, -169.5, -171);
    Playfield.rotate(radians(-Rotation));
    Playfield.translate(-Location.x, -Location.y);
    Playfield.popMatrix();
    Playfield.fill(255);
    Playfield.strokeWeight(1);
  }
  void rc() {
    switch(int(random(0, 3))) {
    case 0: 
      co = #0FFFF1;
      break;
    case 1: 
      co = #FF76F6;
      break;
    case 2: 
      co = #FAE923;
      break;
    }
  }
}