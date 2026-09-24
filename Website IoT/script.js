/* ══════════════════════════════════════════════
   UTILITY
══════════════════════════════════════════════ */
const pad = n => String(n).padStart(2,'0');
function showToast(msg){
  const t=document.getElementById('toast');
  t.textContent=msg; t.classList.add('show');
  clearTimeout(t._t); t._t=setTimeout(()=>t.classList.remove('show'),2800);
}

/* ══════════════════════════════════════════════
   ABOUT 2D CANVAS — isometric device sketch
══════════════════════════════════════════════ */
(function drawAbout(){
  const canvas=document.getElementById('about-canvas');
  if(!canvas)return;
  const ctx=canvas.getContext('2d');
  const w=canvas.width, h=canvas.height;
  let angle=0;
  function draw(){
    ctx.clearRect(0,0,w,h);
    ctx.fillStyle='#0c1218'; ctx.fillRect(0,0,w,h);

    const cx=w/2, cy=h/2+20;
    const s=Math.sin(angle), c=Math.cos(angle);

    // Grid background
    ctx.strokeStyle='rgba(0,229,160,0.04)'; ctx.lineWidth=1;
    for(let i=0;i<w;i+=40){ctx.beginPath();ctx.moveTo(i,0);ctx.lineTo(i,h);ctx.stroke()}
    for(let j=0;j<h;j+=40){ctx.beginPath();ctx.moveTo(0,j);ctx.lineTo(w,j);ctx.stroke()}

    // Solar panel
    const pw=110+c*20, ph=70;
    ctx.fillStyle='#4a5f7f';
    ctx.fillRect(cx-pw/2, cy-170, pw, ph);
    // panel grid lines
    ctx.strokeStyle='#2a3f5f'; ctx.lineWidth=1.5;
    for(let i=1;i<5;i++){
      ctx.beginPath();ctx.moveTo(cx-pw/2+i*pw/5,cy-170);ctx.lineTo(cx-pw/2+i*pw/5,cy-100);ctx.stroke();
    }
    for(let j=1;j<3;j++){
      ctx.beginPath();ctx.moveTo(cx-pw/2,cy-170+j*ph/3);ctx.lineTo(cx-pw/2+pw,cy-170+j*ph/3);ctx.stroke();
    }
    // solar shine
    ctx.fillStyle='rgba(100,180,255,0.12)';
    ctx.fillRect(cx-pw/2+4,cy-166,pw-8,ph-8);

    // Panel label
    ctx.fillStyle='#00e5a0'; ctx.font='bold 11px Space Mono';
    ctx.textAlign='center'; ctx.fillText('PANEL SURYA 10WP',cx,cy-178);

    // Pole
    ctx.fillStyle='#d0d0d0';
    ctx.fillRect(cx-8,cy-100,16,75);
    ctx.strokeStyle='#aaa'; ctx.lineWidth=1;
    ctx.strokeRect(cx-8,cy-100,16,75);

    // Main bucket (cylinder top ellipse)
    ctx.fillStyle='#5a5040';
    ctx.beginPath();
    ctx.ellipse(cx,cy-28,75,18,0,0,Math.PI*2);
    ctx.fill();
    ctx.strokeStyle='#3a3028'; ctx.lineWidth=2; ctx.stroke();
    // Lid detail
    ctx.fillStyle='#4a4030';
    ctx.beginPath();ctx.ellipse(cx,cy-28,65,14,0,0,Math.PI*2);ctx.fill();

    // Bucket body
    const grad=ctx.createLinearGradient(cx-75,cy-28,cx+75,cy+55);
    grad.addColorStop(0,'#c8bfaa'); grad.addColorStop(0.5,'#e8dfc8'); grad.addColorStop(1,'#b8b0a0');
    ctx.fillStyle=grad;
    ctx.beginPath();ctx.ellipse(cx,cy-28,75,18,0,0,Math.PI);
    ctx.lineTo(cx+75,cy+55);
    ctx.ellipse(cx,cy+55,75,18,0,Math.PI,0);
    ctx.closePath(); ctx.fill();
    ctx.strokeStyle='#a0968a'; ctx.lineWidth=2; ctx.stroke();

    // Handle
    ctx.strokeStyle='#888'; ctx.lineWidth=3;
    ctx.beginPath();
    ctx.arc(cx,cy-45,55,Math.PI+0.3,-0.3,false);
    ctx.stroke();

    // Orange bottom cone
    const coneGrad=ctx.createLinearGradient(cx-65,cy+50,cx+65,cy+100);
    coneGrad.addColorStop(0,'#cc6600'); coneGrad.addColorStop(0.5,'#ff8c42'); coneGrad.addColorStop(1,'#cc6600');
    ctx.fillStyle=coneGrad;
    ctx.beginPath();
    ctx.ellipse(cx,cy+55,65,16,0,0,Math.PI);
    ctx.lineTo(cx+25,cy+105);
    ctx.ellipse(cx,cy+105,25,8,0,Math.PI,0);
    ctx.closePath(); ctx.fill();
    ctx.strokeStyle='#aa5500'; ctx.lineWidth=2; ctx.stroke();
    // cone top ellipse
    ctx.fillStyle='#ff9955';
    ctx.beginPath();ctx.ellipse(cx,cy+55,65,16,0,0,Math.PI*2);ctx.fill();

    // Servo/funnel bottom
    ctx.fillStyle='#3a2010';
    ctx.beginPath();
    ctx.moveTo(cx-28,cy+105); ctx.lineTo(cx+28,cy+105);
    ctx.lineTo(cx+18,cy+128); ctx.lineTo(cx-18,cy+128);
    ctx.closePath(); ctx.fill();
    ctx.strokeStyle='#2a1508'; ctx.lineWidth=2; ctx.stroke();

    // Base box
    const baseGrad=ctx.createLinearGradient(cx-80,cy+128,cx+80,cy+195);
    baseGrad.addColorStop(0,'#222'); baseGrad.addColorStop(1,'#111');
    ctx.fillStyle=baseGrad;
    ctx.fillRect(cx-80,cy+128,160,67);
    ctx.strokeStyle='#555'; ctx.lineWidth=2;
    ctx.strokeRect(cx-80,cy+128,160,67);
    // Base highlight
    ctx.strokeStyle='#00e5a0'; ctx.lineWidth=1;
    ctx.strokeRect(cx-78,cy+130,156,63);

    // Motor inside box
    ctx.fillStyle='#333'; ctx.fillRect(cx-30,cy+140,60,18);
    ctx.fillStyle='#444'; ctx.fillRect(cx-20,cy+155,40,15);
    // Motor glow
    ctx.fillStyle='rgba(0,229,160,0.15)';
    ctx.fillRect(cx-25,cy+142,50,14);

    // TDS sensor (small cylinder on box side)
    ctx.fillStyle='#888';
    ctx.fillRect(cx+60,cy+145,16,25);
    ctx.fillStyle='#00aaff';
    ctx.fillRect(cx+62,cy+165,12,5);
    ctx.fillStyle='rgba(0,170,255,0.5)'; ctx.lineWidth=1;
    ctx.strokeStyle='#00aaff'; ctx.beginPath();
    ctx.moveTo(cx+60,cy+155); ctx.lineTo(cx+40,cy+155); ctx.stroke();

    // Labels with lines
    const labels=[
      {x:cx+85,y:cy-100,text:'Wadah HDPE/PP',tx:cx+90},
      {x:cx+85,y:cy+80,text:'Corong Servo',tx:cx+90},
      {x:cx+85,y:cy+158,text:'Motor DC',tx:cx+90},
      {x:cx-85,y:cy+158,text:'Sensor TDS',tx:cx-90},
    ];
    labels.forEach(l=>{
      ctx.fillStyle='#00e5a0'; ctx.font='10px Space Mono';
      ctx.textAlign=l.tx>cx?'left':'right';
      ctx.fillText(l.text,l.x,l.y);
    });

    angle+=0.008;
    requestAnimationFrame(draw);
  }
  draw();
})();

/* ══════════════════════════════════════════════
   THREE.JS — 3D MODEL
══════════════════════════════════════════════ */
(function init3D(){
  const wrap=document.getElementById('canvas3d-wrap');
  const W=wrap.clientWidth||900, H=520;

  const scene=new THREE.Scene();
  scene.background=new THREE.Color(0x0c1218);
  scene.fog=new THREE.Fog(0x0c1218,20,60);

  const camera=new THREE.PerspectiveCamera(55,W/H,0.1,1000);
  camera.position.set(3,4,10);

  const renderer=new THREE.WebGLRenderer({antialias:true});
  renderer.setSize(W,H);
  renderer.setPixelRatio(Math.min(window.devicePixelRatio,2));
  renderer.shadowMap.enabled=true;
  wrap.insertBefore(renderer.domElement,wrap.firstChild);

  // Lights
  const amb=new THREE.AmbientLight(0xffffff,0.6); scene.add(amb);
  const dir=new THREE.DirectionalLight(0xffffff,0.9);
  dir.position.set(8,12,8); dir.castShadow=true; scene.add(dir);
  const pt=new THREE.PointLight(0x00e5a0,0.8,30);
  pt.position.set(0,2,5); scene.add(pt);
  const pt2=new THREE.PointLight(0x0044ff,0.4,20);
  pt2.position.set(-5,0,-3); scene.add(pt2);

  const model=new THREE.Group();

  function mat(col,rough=0.5,met=0,trans=false,op=1){
    return new THREE.MeshStandardMaterial({color:col,roughness:rough,metalness:met,transparent:trans,opacity:op});
  }

  // Base platform
  const baseGeo=new THREE.BoxGeometry(4,0.9,3.2);
  const baseMesh=new THREE.Mesh(baseGeo,mat(0x111111,0.8));
  baseMesh.position.y=-4.8; baseMesh.castShadow=true;
  model.add(baseMesh);

  // Base edge glow strip
  const glowGeo=new THREE.BoxGeometry(4.05,0.05,3.25);
  const glowMesh=new THREE.Mesh(glowGeo,mat(0x00e5a0,0.2));
  glowMesh.position.y=-4.35; model.add(glowMesh);

  // Motor inside base
  const motGeo=new THREE.CylinderGeometry(0.6,0.6,0.5,16);
  const motMesh=new THREE.Mesh(motGeo,mat(0x333333,0.5,0.5));
  motMesh.position.set(0,-4.7,0); model.add(motMesh);

  // Support pole
  const poleGeo=new THREE.CylinderGeometry(0.18,0.18,5.5,12);
  const poleMesh=new THREE.Mesh(poleGeo,mat(0xdddddd,0.4,0.6));
  poleMesh.position.y=0.25; model.add(poleMesh);

  // Cone/funnel (orange bottom)
  const funnelGeo=new THREE.ConeGeometry(2.0,2.2,32);
  const funnelMesh=new THREE.Mesh(funnelGeo,mat(0xff8c42,0.45));
  funnelMesh.position.y=-2.5; model.add(funnelMesh);

  // Main cylinder bucket
  const buckGeo=new THREE.CylinderGeometry(2.2,2.2,2.8,40);
  const buckMesh=new THREE.Mesh(buckGeo,mat(0xd4c8b0,0.55));
  buckMesh.position.y=0.3; model.add(buckMesh);

  // Bucket handle
  const curve=new THREE.CatmullRomCurve3([
    new THREE.Vector3(-2.2,1.2,0),
    new THREE.Vector3(-1.5,2.5,0),
    new THREE.Vector3(0,3,0),
    new THREE.Vector3(1.5,2.5,0),
    new THREE.Vector3(2.2,1.2,0),
  ]);
  const hGeo=new THREE.TubeGeometry(curve,20,0.1,8,false);
  const handleMesh=new THREE.Mesh(hGeo,mat(0x888888,0.5,0.5));
  model.add(handleMesh);

  // Lid (dark)
  const lidGeo=new THREE.CylinderGeometry(2.25,2.25,0.25,40);
  const lidMesh=new THREE.Mesh(lidGeo,mat(0x4a3830,0.6));
  lidMesh.position.y=1.8; model.add(lidMesh);

  // Lid inner
  const lid2Geo=new THREE.CylinderGeometry(1.9,1.9,0.1,40);
  const lid2Mesh=new THREE.Mesh(lid2Geo,mat(0x5a4840,0.7));
  lid2Mesh.position.y=1.96; model.add(lid2Mesh);

  // Solar panel frame
  const sFrameGeo=new THREE.BoxGeometry(6.5,3.5,0.15);
  const sFrameMesh=new THREE.Mesh(sFrameGeo,mat(0x888888,0.4,0.7));
  sFrameMesh.position.set(0,5.5,0); model.add(sFrameMesh);

  // Solar panel cells
  const sPanelGeo=new THREE.BoxGeometry(6.1,3.1,0.12);
  const sPanelMesh=new THREE.Mesh(sPanelGeo,mat(0x3a4e7a,0.3));
  sPanelMesh.position.set(0,5.5,0.02); model.add(sPanelMesh);

  // Panel grid lines (emissive)
  for(let i=-2;i<=2;i++){
    const g=new THREE.BoxGeometry(0.04,3.1,0.02);
    const m=new THREE.Mesh(g,new THREE.MeshStandardMaterial({color:0x5a6f9a,roughness:0.2}));
    m.position.set(i*1.2,5.5,0.09); model.add(m);
  }
  for(let j=-1;j<=1;j++){
    const g=new THREE.BoxGeometry(6.1,0.04,0.02);
    const m=new THREE.Mesh(g,new THREE.MeshStandardMaterial({color:0x5a6f9a,roughness:0.2}));
    m.position.set(0,5.5+j*1.0,0.09); model.add(m);
  }

  // TDS sensor (small on base side)
  const tdsGeo=new THREE.BoxGeometry(0.25,0.8,0.25);
  const tdsMesh=new THREE.Mesh(tdsGeo,mat(0x555555,0.5,0.3));
  tdsMesh.position.set(2.1,-4.4,0.5); model.add(tdsMesh);
  const tdsCapGeo=new THREE.CylinderGeometry(0.12,0.12,0.15,8);
  const tdsCapMesh=new THREE.Mesh(tdsCapGeo,mat(0x0088ff,0.2));
  tdsCapMesh.position.set(2.1,-4.85,0.5); model.add(tdsCapMesh);

  // Ground plane
  const groundGeo=new THREE.PlaneGeometry(30,30);
  const groundMesh=new THREE.Mesh(groundGeo,mat(0x080d12,0.9));
  groundGeo.rotateX(-Math.PI/2); groundMesh.position.y=-5.3; groundMesh.receiveShadow=true;
  scene.add(groundMesh);

  // Grid helper
  const grid=new THREE.GridHelper(20,20,0x00e5a0,0x0a1218);
  grid.position.y=-5.28; scene.add(grid);

  scene.add(model);

  // Mouse interaction
  let isDrag=false,pX=0,pY=0,rotY=0,rotX=0;
  const el=renderer.domElement;
  el.style.cursor='grab';
  el.addEventListener('mousedown',e=>{isDrag=true;pX=e.clientX;pY=e.clientY;el.style.cursor='grabbing'});
  window.addEventListener('mouseup',()=>{isDrag=false;el.style.cursor='grab'});
  window.addEventListener('mousemove',e=>{
    if(!isDrag)return;
    rotY+=(e.clientX-pX)*0.012; rotX+=(e.clientY-pY)*0.008;
    rotX=Math.max(-0.8,Math.min(0.8,rotX));
    pX=e.clientX; pY=e.clientY;
  });
  el.addEventListener('wheel',e=>{
    e.preventDefault();
    camera.position.z=Math.max(4,Math.min(18,camera.position.z+e.deltaY*0.02));
  },{passive:false});
  el.addEventListener('dblclick',()=>{rotX=0;rotY=0;camera.position.z=10});

  // Touch support
  let tStart=null;
  el.addEventListener('touchstart',e=>{tStart=e.touches[0];isDrag=true;pX=tStart.clientX;pY=tStart.clientY});
  el.addEventListener('touchmove',e=>{
    if(!isDrag)return;
    e.preventDefault();
    const t=e.touches[0];
    rotY+=(t.clientX-pX)*0.012; rotX+=(t.clientY-pY)*0.008;
    rotX=Math.max(-0.8,Math.min(0.8,rotX));
    pX=t.clientX; pY=t.clientY;
  },{passive:false});
  el.addEventListener('touchend',()=>{isDrag=false});

  // Animate
  const clock=new THREE.Clock();
  function animate(){
    requestAnimationFrame(animate);
    const t=clock.getElapsedTime();
    if(!isDrag) rotY+=0.003;
    model.rotation.y=rotY;
    model.rotation.x=rotX;
    // Solar panel slight float
    model.position.y=Math.sin(t*0.5)*0.05;
    // TDS sensor glow pulse
    pt.intensity=0.6+Math.sin(t*2)*0.3;
    renderer.render(scene,camera);
  }
  animate();

  window.addEventListener('resize',()=>{
    const nw=wrap.clientWidth;
    camera.aspect=nw/H; camera.updateProjectionMatrix();
    renderer.setSize(nw,H);
  });
})();

/* ══════════════════════════════════════════════
   DASHBOARD
══════════════════════════════════════════════ */
function updateClock(){
  const n=new Date();
  document.getElementById('clk').textContent=pad(n.getHours())+':'+pad(n.getMinutes())+':'+pad(n.getSeconds())+' WIB';
  const tot=n.getHours()*60+n.getMinutes();
  const acts=schedules.filter(s=>s.on).map(s=>s.h*60+s.m).sort((a,b)=>a-b);
  if(!acts.length){document.getElementById('m-next').textContent='—';return}
  let nxt=acts.find(t=>t>tot);
  let prog;
  if(!nxt){nxt=acts[0];prog=1-(24*60-tot+nxt)/(24*60)}
  else{const prv=acts.filter(t=>t<=tot).pop()??(acts[acts.length-1]-24*60);prog=(tot-prv)/(nxt-prv)}
  const nh=Math.floor(nxt/60),nm=nxt%60;
  document.getElementById('m-next').textContent=pad(nh)+':'+pad(nm);
  document.getElementById('b-next').style.width=Math.min(Math.max(prog*100,0),100)+'%';
}

const dbLogs=[
  '[ <span style="color:var(--acc)">OK</span> ] Sistem normal — NTP sync berhasil',
  '[ <span style="color:var(--acc)">INFO</span> ] Solar charging aktif — cuaca cerah',
  '[ <span style="color:var(--acc)">OK</span> ] Jadwal berikutnya terdaftar',
  '[ <span style="color:var(--acc)">INFO</span> ] Sensor ultrasonik HC-SR04 aktif',
];
let dbLogIdx=0;

function refreshDash(){
  const tds=Math.floor(260+Math.random()*200);
  const stok=Math.floor(60+Math.random()*38);
  const bat=Math.floor(72+Math.random()*26);
  const ok=tds<500;
  document.getElementById('m-tds').textContent=tds;
  document.getElementById('m-stok').textContent=stok;
  document.getElementById('m-bat').textContent=bat;
  document.getElementById('m-tds').className='metric-val '+(ok?'ok':'danger');
  const tb=document.getElementById('b-tds');
  tb.style.width=Math.min((tds/500)*100,100)+'%';
  tb.style.background=ok?'var(--acc)':'var(--err)';
  document.getElementById('b-stok').style.width=stok+'%';
  document.getElementById('b-bat').style.width=bat+'%';
  dbLogIdx=(dbLogIdx+1)%dbLogs.length;
  document.getElementById('log-line').innerHTML=ok
    ?dbLogs[dbLogIdx]
    :'[ <span style="color:var(--err)">WARN</span> ] TDS '+tds+' PPM — melebihi 500! Notifikasi dikirim ke pembudidaya.';
}

setInterval(updateClock,1000);
setInterval(refreshDash,5500);
updateClock(); refreshDash();

/* ══════════════════════════════════════════════
   JADWAL PAKAN
══════════════════════════════════════════════ */
const DEF_SCHED=[{id:1,h:5,m:0,lbl:'Pagi',on:true},{id:2,h:16,m:30,lbl:'Sore',on:true}];
let schedules=JSON.parse(JSON.stringify(DEF_SCHED));
let nxtId=3, editId=null;

function renderSched(){
  const el=document.getElementById('sched-list');
  if(!schedules.length){el.innerHTML='<div style="padding:1.5rem;text-align:center;color:var(--tx3);font-family:var(--mono);font-size:.78rem">Belum ada jadwal — klik + Tambah</div>';return}
  const sorted=[...schedules].sort((a,b)=>a.h*60+a.m-(b.h*60+b.m));
  el.innerHTML=sorted.map(s=>{
    const isEd=editId===s.id;
    return `<div class="sched-row">
      <div class="tog${s.on?' on':''}" onclick="togSched(${s.id})"></div>
      ${isEd
        ?`<input class="finput" style="width:48px;font-size:.85rem;padding:.3rem" id="eh${s.id}" type="number" min="0" max="23" value="${s.h}"><span style="color:var(--tx3);margin:0 .3rem;font-family:var(--mono)">:</span><input class="finput" style="width:48px;font-size:.85rem;padding:.3rem" id="em${s.id}" type="number" min="0" max="59" value="${s.m}">`
        :`<div class="sched-time" style="${s.on?'':'color:var(--tx3)'}">${pad(s.h)}:${pad(s.m)}</div>`
      }
      <span class="sched-lbl" style="${s.on?'':'opacity:.45'}">${s.lbl}</span>
      <span class="sched-stat ${s.on?'on':'off'}">${s.on?'● AKTIF':'○ OFF'}</span>
      ${isEd
        ?`<button class="ibu ed" onclick="saveSched(${s.id})" title="Simpan">✓</button><button class="ibu" onclick="editId=null;renderSched()" style="color:var(--tx3)" title="Batal">✕</button>`
        :`<button class="ibu ed" onclick="editId=${s.id};renderSched()" title="Edit">✎</button>`
      }
      <button class="ibu" onclick="delSched(${s.id})" title="Hapus">✕</button>
    </div>`;
  }).join('');
}
function togSched(id){const s=schedules.find(x=>x.id===id);if(s){s.on=!s.on;renderSched();updateClock()}}
function saveSched(id){
  const s=schedules.find(x=>x.id===id);
  const h=+document.getElementById('eh'+id).value;
  const m=+document.getElementById('em'+id).value;
  if(h<0||h>23||m<0||m>59){showToast('⚠ Waktu tidak valid!');return}
  s.h=h;s.m=m;editId=null;renderSched();updateClock();showToast('Jadwal diperbarui ✓');
}
function delSched(id){
  if(!confirm('Hapus jadwal ini?'))return;
  schedules=schedules.filter(x=>x.id!==id);
  renderSched();updateClock();
}
function showAdd(){document.getElementById('add-box').style.display='block';document.getElementById('in-lbl').focus()}
function hideAdd(){document.getElementById('add-box').style.display='none'}
function addJadwal(){
  const h=+document.getElementById('in-h').value;
  const m=+document.getElementById('in-m').value;
  const lbl=document.getElementById('in-lbl').value.trim()||'Jadwal';
  if(h<0||h>23||m<0||m>59){showToast('⚠ Waktu tidak valid!');return}
  schedules.push({id:nxtId++,h,m,lbl,on:true});
  document.getElementById('in-h').value=8;
  document.getElementById('in-m').value=0;
  document.getElementById('in-lbl').value='';
  hideAdd();renderSched();updateClock();showToast('Jadwal ditambahkan ✓');
}
function resetJadwal(){
  if(!confirm('Reset semua jadwal ke setelan pabrik?\n(05:00 & 16:30)'))return;
  schedules=JSON.parse(JSON.stringify(DEF_SCHED));
  nxtId=3;editId=null;hideAdd();renderSched();updateClock();showToast('Jadwal direset ✓');
}
renderSched();

/* ══════════════════════════════════════════════
   SERVO CONTROL
══════════════════════════════════════════════ */
function doServo(v){
  v=+v;
  document.getElementById('srv-slider').value=v;
  document.getElementById('srv-disp').textContent=v+'°';
  document.getElementById('srv-angle').textContent=v+'°';
  // Arc circumference ≈ 427 for r=68
  document.getElementById('srv-arc').setAttribute('stroke-dashoffset',(427*(1-v/180)).toFixed(1));
  let icon,state,col;
  if(v===0){icon='🔒';state='TERTUTUP';col='var(--tx3)'}
  else if(v<60){icon='🔓';state='SEDIKIT BUKA';col='var(--warn)'}
  else if(v<140){icon='💧';state='SETENGAH BUKA';col='var(--warn)'}
  else{icon='🌊';state='TERBUKA PENUH';col='var(--acc)'}
  document.getElementById('srv-icon').textContent=icon;
  const stEl=document.getElementById('srv-state');
  stEl.textContent=state; stEl.style.color=col;
  document.getElementById('srv-angle').style.color=v>0?'var(--acc)':'var(--tx3)';
  document.getElementById('srv-status').textContent=v===0?'STANDBY — 0° (TERTUTUP)':'AKTIF — '+v+'° · '+state;
  document.getElementById('srv-status').style.color=v>0?'var(--acc)':'var(--tx2)';
  document.querySelectorAll('.preset-btn').forEach(b=>{
    b.classList.toggle('active',+b.dataset.v===v);
  });
}
function setServo(v){doServo(v)}
function resetServo(){doServo(0);showToast('Servo direset ke 0° (tertutup) ✓')}
doServo(0);

/* ══════════════════════════════════════════════
   AI CHATBOT
══════════════════════════════════════════════ */
let chatOpen=false;
let convHistory=[];
const SYSTEM_PROMPT=`Kamu adalah AquaBot, AI asisten jenius untuk sistem Smart Aquaculture V3. Kamu berpengetahuan sangat luas — bukan hanya tentang sistem ini, tapi juga sains, teknologi, sejarah, filsafat, matematika, budidaya, IoT, programming, dan topik apapun yang ditanyakan.

Tentang sistem ini:
- Smart Aquaculture V3 adalah alat budidaya ikan otomatis berbasis IoT
- Mikrokontroler: ESP32
- Energi: Off-Grid dengan Solar 10WP + Aki 12V/5Ah + Solar Charge Controller
- Sensor: TDS (kepekatan air, alert >500 PPM) + HC-SR04 Ultrasonik (estimasi stok pakan)
- Aktuator: Motor Servo (katup pakan) + Motor DC (penyebaran sentrifugal)
- Jadwal otomatis: 05:00 WIB & 16:30 WIB, tersinkronisasi NTP global
- Wadah: HDPE/PP food-safe, waterproof, UV-resistant
- Dikembangkan oleh Arsad Azami Nursamal, SIJA, SMKN 2 Yogyakarta

Gaya bicara kamu: cerdas namun hangat, jawaban mendalam tapi tidak membosankan, kadang pakai analogi yang kreatif. Selalu antusias membantu. Jawab dalam Bahasa Indonesia yang natural dan mengalir. Maksimal 3 paragraf singkat per respons kecuali diminta lebih panjang. Gunakan emoji sesekali untuk keakraban.`;

function toggleChat(){
  chatOpen=!chatOpen;
  document.getElementById('chat-win').classList.toggle('open',chatOpen);
  if(chatOpen){
    document.getElementById('chat-badge').style.display='none';
    if(convHistory.length===0) addBotMsg('Halo! Saya **AquaBot** 🤖 — asisten AI dari Smart Aquaculture V3.\n\nSaya siap berdiskusi tentang apapun: sistem ini, IoT, budidaya ikan, sains, teknologi, atau topik favorit kamu. Apa yang ingin kamu ketahui?');
  }
}

function addBotMsg(text){
  const msgs=document.getElementById('chat-msgs');
  const div=document.createElement('div');
  div.className='msg msg-bot';
  // Simple markdown: **bold**
  div.innerHTML=text.replace(/\*\*(.*?)\*\*/g,'<strong>$1</strong>').replace(/\n/g,'<br>');
  msgs.appendChild(div);
  msgs.scrollTop=msgs.scrollHeight;
}

function addUserMsg(text){
  const msgs=document.getElementById('chat-msgs');
  const div=document.createElement('div');
  div.className='msg msg-user';
  div.textContent=text;
  msgs.appendChild(div);
  msgs.scrollTop=msgs.scrollHeight;
}

function showTyping(){
  const msgs=document.getElementById('chat-msgs');
  const div=document.createElement('div');
  div.className='msg msg-typing'; div.id='typing-ind';
  div.innerHTML='<span id="typing-dots">⬤ ⬤ ⬤</span>';
  msgs.appendChild(div); msgs.scrollTop=msgs.scrollHeight;
  let d=0; div._iv=setInterval(()=>{
    const dots=['⬤ ⬤ ⬤','◯ ⬤ ⬤','⬤ ◯ ⬤','⬤ ⬤ ◯'];
    document.getElementById('typing-dots').textContent=dots[d++%4];
  },400);
  return div;
}

function removeTyping(div){
  if(div._iv)clearInterval(div._iv);
  div.remove();
}

async function sendMsg(){
  const inp=document.getElementById('chat-input');
  const text=inp.value.trim();
  if(!text)return;
  inp.value='';
  document.getElementById('chat-send-btn').disabled=true;
  document.getElementById('quick-btns').style.display='none';

  addUserMsg(text);
  convHistory.push({role:'user',content:text});

  const typing=showTyping();

  // Catatan: memanggil API Claude langsung dari browser tidak aman (API key akan
  // terekspos ke publik) dan akan diblokir CORS. Untuk demo statis ini, AquaBot
  // memakai mesin jawaban berbasis aturan yang berjalan sepenuhnya di browser.
  // Untuk jawaban AI sungguhan, sambungkan ke backend Anda sendiri yang menyimpan
  // API key dengan aman dan memanggil Anthropic API dari sisi server.
  await new Promise(r=>setTimeout(r,650+Math.random()*450));
  removeTyping(typing);
  const reply=getFallback(text);
  convHistory.push({role:'assistant',content:reply});
  addBotMsg(reply);

  document.getElementById('chat-send-btn').disabled=false;
  document.getElementById('chat-input').focus();
}
function getFallback(q){
  const lq=q.toLowerCase();
  if(lq.includes('tds'))return 'Sensor TDS (Total Dissolved Solids) mengukur jumlah partikel terlarut dalam air — termasuk amonia, nitrat, dan mineral. Untuk budidaya ikan, batas aman umumnya di bawah 500 PPM. Jika melebihi itu, ikan bisa mengalami stres atau keracunan! 🐟';
  if(lq.includes('esp32'))return 'ESP32 dipilih karena merupakan mikrokontroler canggih dengan WiFi dan Bluetooth built-in, harga terjangkau, dan komunitas developer yang besar. Dengan processor dual-core 240MHz, ESP32 mampu menjalankan semua logika sistem ini dengan mudah! ⚡';
  if(lq.includes('solar')||lq.includes('surya'))return 'Panel surya 10WP dipilih karena cukup untuk kebutuhan daya sistem yang hemat energi ini. Dikombinasikan dengan Aki 12V/5Ah, sistem bisa tetap beroperasi bahkan di malam hari atau saat cuaca mendung. Inilah inti dari konsep Off-Grid! ☀️';
  if(lq.includes('servo'))return 'Motor servo adalah aktuator yang bisa diposisikan pada sudut tertentu (0°–180°). Dalam sistem ini, servo berfungsi sebagai katup presisi yang mengontrol aliran pakan dari wadah ke corong distribusi. Saat jadwal tiba, servo terbuka otomatis! 🔧';
  if(lq.includes('cara kerja')||lq.includes('bekerja'))return 'Alurnya begini: panel surya mengisi aki lewat SCC → ESP32 sinkron waktu via NTP → saat jadwal tiba, servo membuka katup → pakan jatuh ke motor DC yang berputar cepat → pakan tersebar merata secara sentrifugal ke kolam. Semua otomatis, tanpa sentuhan manusia! 🔄';
  if(lq.includes('budidaya')||lq.includes('tips'))return 'Beberapa tips dasar budidaya ikan: jaga kualitas air (pantau TDS, pH, suhu), beri pakan secukupnya di waktu konsisten, hindari overfeeding karena sisa pakan mencemari air, dan lakukan pergantian air berkala. Sistem otomatis seperti ini membantu menjaga konsistensi jadwal pakan! 🐠';
  return 'Pertanyaan yang menarik! Saya AquaBot, asisten dari Smart Aquaculture V3. Saya bisa berdiskusi soal sistem ini, IoT, budidaya ikan, atau topik teknis lain. Coba tanya lebih spesifik ya, supaya saya bisa kasih jawaban terbaik! 🤖';
}

function quickAsk(text){
  document.getElementById('chat-input').value=text;
  sendMsg();
}

/* ══════════════════════════════════════════════
   SCROLL ANIMATIONS
══════════════════════════════════════════════ */
const obs=new IntersectionObserver(entries=>{
  entries.forEach(e=>{if(e.isIntersecting)e.target.classList.add('vis')});
},{threshold:0.12});
document.querySelectorAll('.anim').forEach(el=>obs.observe(el));
