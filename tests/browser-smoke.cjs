/* Optional browser verification: npm install playwright, then node tests/browser-smoke.cjs.
 * Start a separate test database first. This creates and removes temporary records.
 * HOTEL_TEST_URL selects the local server; PLAYWRIGHT_BROWSERS_PATH selects browser files.
 */
const {chromium}=require('playwright');
const fs=require('node:fs');
const assert=require('node:assert/strict');
const base=process.env.HOTEL_TEST_URL || 'http://127.0.0.1:8095';
(async()=>{
 const browser=await chromium.launch({headless:true});
 const page=await browser.newPage({viewport:{width:1440,height:1000}});
 const errors=[];page.on('pageerror',e=>errors.push(e.message));
 const checks=[];
 const out='docs/screenshots';fs.mkdirSync(out,{recursive:true});
 await page.goto(base);await page.getByRole('heading',{name:'Welcome back.'}).waitFor();
 await page.screenshot({path:out+'/01-login.png',fullPage:true});
 await page.getByLabel('Email address').fill('manager@serene.local');await page.getByLabel('Password',{exact:true}).fill('Serene@2026');
 await page.getByRole('button',{name:'Sign in to workspace'}).click();await page.getByRole('heading',{name:/Good (morning|afternoon|evening)/}).waitFor();
 await page.screenshot({path:out+'/02-dashboard.png',fullPage:true});checks.push('Manager sign-in, JSP and dashboard rendering');
 async function request(path,method='GET',data,token=true){return await page.evaluate(async({path,method,data,token})=>{
  const session=await (await fetch('/api/auth/session')).json();
  const r=await fetch('/api/'+path,{method,headers:{'Content-Type':'application/json',...(token?{'X-CSRF-Token':session.csrf}:{})},...(data!==undefined?{body:JSON.stringify(data)}:{})});
  return {status:r.status,body:await r.json()};
 },{path,method,data,token});}
 const ids={};const suffix=Date.now();
 const data={
  guests:{name:'Browser Guest',email:`browser${suffix}@example.com`,phone:'0771234567',type:'VIP',discount_rate:.1},
  rooms:{number:'B'+String(suffix).slice(-8),type:'SUITE',capacity:4,base_price:10000,luxury_tax:.1,status:'AVAILABLE'},
  staff:{name:'Browser Agent',email:`browserstaff${suffix}@example.com`,phone:'0771234567',role:'RECEPTIONIST',shift:'MORNING',password:'BrowserPass123'},
  services:{name:'Browser service '+suffix,category:'WELLNESS',type:'PAID',base_cost:1000,status:'ACTIVE',description:'Browser integration amenity'}
 };
 for(const key of ['guests','rooms','staff','services']){const r=await request(key,'POST',data[key]);assert.equal(r.status,201,JSON.stringify(r));ids[key]=r.body.id;}
 data.reservations={guest_id:ids.guests,room_id:ids.rooms,check_in:'2026-09-01',check_out:'2026-09-03',party_size:2,type:'ADVANCE',status:'CHECKED_OUT'};
 let r=await request('reservations','POST',data.reservations);assert.equal(r.status,201,JSON.stringify(r));ids.reservations=r.body.id;
 data.reviews={reservation_id:ids.reservations,author:'Browser Guest',type:'VERIFIED',rating:5,text:'A browser-tested stay.',status:'PUBLISHED'};
 r=await request('reviews','POST',data.reviews);assert.equal(r.status,201,JSON.stringify(r));ids.reviews=r.body.id;
 for(const key of Object.keys(ids)){
  r=await request(`${key}/${ids[key]}`);assert.equal(r.status,200);assert.equal(Number(r.body.id),ids[key]);
  if(key==='reservations')assert.equal(r.body.total,19800);
  r=await request(`${key}/${ids[key]}`,'PUT',data[key]);assert.equal(r.status,200,JSON.stringify(r));
 }
 checks.push('HTTP create, read and update for all six resources; suite/VIP total LKR 19,800');
 assert.equal((await request('guests','POST',data.guests,false)).status,403);checks.push('Missing CSRF token blocked');
 const booking={...data.reservations,check_in:'2026-11-01',check_out:'2026-11-03',status:'CONFIRMED'};
 r=await request('reservations','POST',booking);assert.equal(r.status,201);const active=r.body.id;
 assert.equal((await request('reservations','POST',booking)).status,400);
 assert.equal((await request(`reservations/${active}`,'DELETE')).status,400);
 assert.equal((await request(`guests/${ids.guests}`,'DELETE')).status,409);
 booking.status='CANCELLED';assert.equal((await request(`reservations/${active}`,'PUT',booking)).status,200);
 assert.equal((await request(`reservations/${active}`,'DELETE')).status,200);checks.push('Overlap, active-delete and foreign-key protection; cancellation releases booking');
 for(const key of ['guests','rooms','reservations','staff','services','reviews']){
  await page.goto(base+`/#/${key}`);await page.locator('#table-container').waitFor();assert.equal(await page.locator('.error').count(),0);
  if(key==='reservations')await page.screenshot({path:out+'/03-reservations.png',fullPage:true});
  await page.goto(base+`/#/${key}/new`);await page.locator('#record-form').waitFor();assert.equal(await page.locator('.error').count(),0);
  await page.goto(base+`/#/${key}/${ids[key]}/edit`);await page.locator('#record-form').waitFor();assert.equal(await page.locator('.error').count(),0);
  if(key==='reservations')await page.screenshot({path:out+'/04-reservation-form.png',fullPage:true});
 }
 checks.push('All 18 list/create/edit routes render without JavaScript errors');
 // Exercise a real UI create, search, edit and confirmed delete sequence.
 await page.goto(base+'/#/guests/new');await page.locator('#record-form').waitFor();
 await page.getByLabel('Full name').fill('UI Guest');await page.getByLabel('Email address').fill(`ui${suffix}@example.com`);await page.getByLabel('Phone number').fill('0777654321');
 await page.getByRole('button',{name:'Create guest',exact:true}).click();await page.locator('#table-container').waitFor();
 await page.getByRole('searchbox').fill(`ui${suffix}`);await page.getByRole('link',{name:/Edit guest/}).click();await page.locator('#record-form').waitFor();
 await page.getByLabel('Full name').fill('UI Guest Updated');await page.getByRole('button',{name:'Save changes'}).click();await page.locator('#table-container').waitFor();
 await page.getByRole('searchbox').fill(`ui${suffix}`);await page.getByRole('button',{name:/Delete guest/}).click();await page.getByRole('button',{name:'Delete record',exact:true}).click();
 await page.getByText('Guest deleted.',{exact:true}).waitFor();await page.reload();await page.locator('#table-container').waitFor();await page.getByRole('searchbox').fill(`ui${suffix}`);await page.getByRole('heading',{name:'No guests found'}).waitFor();
 checks.push('Actual guest UI create/search/edit/delete flow');
 for(const key of Object.keys(ids)) {
  const result=await page.evaluate(async key=>{const r=await fetch('/api/'+key+'/export');return {status:r.status,body:await r.text()};},key);
  assert.equal(result.status,200);assert.ok(Array.isArray(JSON.parse(result.body)));assert.ok(!result.body.includes('password_hash'));
 }
 checks.push('All six file export endpoints return readable JSON text without password hashes');
 await page.setViewportSize({width:390,height:844});await page.goto(base+'/#/dashboard');await page.getByRole('heading',{name:/Good (morning|afternoon|evening)/}).waitFor();
 assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));await page.screenshot({path:out+'/05-mobile-dashboard.png',fullPage:true});
 await page.goto(base+'/#/reservations/new');await page.locator('#record-form').waitFor();assert.ok(await page.evaluate(()=>document.documentElement.scrollWidth<=innerWidth));await page.screenshot({path:out+'/06-mobile-form.png',fullPage:true});
 checks.push('390px mobile dashboard and reservation form: no page overflow');
 for(const key of ['reviews','reservations','guests','rooms','staff','services']){r=await request(`${key}/${ids[key]}`,'DELETE');assert.equal(r.status,200,JSON.stringify(r));}
 checks.push('HTTP delete for all six resources');
 await request('auth/logout','POST',{});await page.goto(base);await page.getByRole('heading',{name:'Welcome back.'}).waitFor();
 await page.getByLabel('Email address').fill('desk@serene.local');await page.getByLabel('Password',{exact:true}).fill('Welcome@2026');await page.getByRole('button',{name:'Sign in to workspace'}).click();await page.getByRole('heading',{name:/Good (morning|afternoon|evening)/}).waitFor();
 assert.equal((await request('staff')).status,403);assert.equal((await request('rooms','POST',data.rooms)).status,403);assert.equal((await request('guests')).status,200);
 checks.push('Receptionist sign-in and server-side role restrictions');
 const anonymous=await browser.newContext();const res=await anonymous.request.get(base+'/api/guests');assert.equal(res.status(),401);await anonymous.close();
 checks.push('Unauthenticated data access blocked');assert.deepEqual(errors,[]);
 const report={verifiedAt:new Date().toISOString(),base,checks,javascriptErrors:errors};fs.writeFileSync('docs/browser-results.json',JSON.stringify(report,null,2));console.log(JSON.stringify(report,null,2));
 await browser.close();
})().catch(e=>{console.error(e);process.exit(1);});
