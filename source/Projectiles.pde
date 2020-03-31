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
  void Render() {
    Playfield.fill(255);
    Playfield.stroke(255);
    Playfield.translate(Location.x, Location.y);
    Playfield.rotate(radians(Rotation));
    Playfield.rect(0, 0, 4, 2);
    Playfield.rotate(radians(-Rotation));
    Playfield.translate(-Location.x, -Location.y);
  }
  void Update() {
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