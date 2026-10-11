'use strict';
// No UI frameworks or remote assets: the workspace also works without internet access.
const $ = (selector, root = document) => root.querySelector(selector);
const esc = value => String(value ?? '').replace(/[&<>"']/g, c => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c]));
const money = value => new Intl.NumberFormat('en-LK',{style:'currency',currency:'LKR',maximumFractionDigits:2}).format(Number(value || 0));
const title = value => String(value || '').toLowerCase().replace(/_/g,' ').replace(/\b\w/g,c=>c.toUpperCase());
const paths = {
 sun:'<path d="M3 18h18M6 14a6 6 0 0 1 12 0M12 2v3M2 9l3 2M22 9l-3 2"/>',
 dashboard:'<rect x="3" y="3" width="7" height="7" rx="1.5"/><rect x="14" y="3" width="7" height="7" rx="1.5"/><rect x="3" y="14" width="7" height="7" rx="1.5"/><rect x="14" y="14" width="7" height="7" rx="1.5"/>',
 reservations:'<rect x="3" y="5" width="18" height="16" rx="2"/><path d="M7 3v4M17 3v4M3 10h18M8 15h3M14 15h2"/>',
 guests:'<circle cx="9" cy="8" r="3"/><path d="M3 21v-3a6 6 0 0 1 12 0v3M16 5a3 3 0 0 1 0 6M18 15a5 5 0 0 1 3 5"/>',
 rooms:'<path d="M3 20V7M21 20V7M3 16h18M3 11h18v5M6 7h5v4M14 7h5v4"/>',
 staff:'<rect x="4" y="5" width="16" height="16" rx="2"/><path d="M9 5V3h6v2M8 16h8"/><circle cx="12" cy="11" r="2"/>',
 services:'<path d="m12 3 3 6 6 3-6 3-3 6-3-6-6-3 6-3z"/>',
 reviews:'<path d="M21 11a9 9 0 0 1-9 9H4l-2 2 1-7a9 9 0 1 1 18-4Z"/><path d="M8 10h8M8 14h5"/>',
 plus:'<path d="M12 5v14M5 12h14"/>', arrow:'<path d="M4 12h16m-5-5 5 5-5 5"/>',
 search:'<circle cx="10" cy="10" r="6"/><path d="m15 15 5 5"/>', edit:'<path d="m15 4 5 5M4 20l5-1L21 7a2 2 0 0 0-4-4L5 15z"/>',
 trash:'<path d="M3 6h18M9 6V3h6v3M6 6l1 15h10l1-15M10 10v7M14 10v7"/>',
 download:'<path d="M12 3v12m-5-5 5 5 5-5M4 16v5h16v-5"/>', logout:'<path d="M10 4H4v16h6M10 12h11m-4-4 4 4-4 4"/>', menu:'<path d="M4 6h16M4 12h16M4 18h16"/>',
 check:'<path d="m5 12 4 4L19 6"/>', back:'<path d="M20 12H4m5-5-5 5 5 5"/>'
};
const icon = name => `<svg viewBox="0 0 24 24" aria-hidden="true">${paths[name] || paths.services}</svg>`;
const brand = `<div class="brand">${icon('sun')}<span>serene<small>HOTEL & RESIDENCES</small></span></div>`;
const modules = window.HotelModules;
const state = {user:null,csrf:'',data:{},routeVersion:0};
async function api(path,options={}) {
 const response = await fetch(`/api/${path}`,{credentials:'same-origin',...options,headers:{'Content-Type':'application/json','X-CSRF-Token':state.csrf,...options.headers}});
 const data = await response.json();
 if(!response.ok) {if(response.status===401 && state.user){state.user=null;login();}throw new Error(data.error || 'The request could not be completed.');}
 return data;
}
function toast(message) {const node=document.createElement('div');node.className='toast';node.textContent=message;$('#toasts').append(node);setTimeout(()=>node.remove(),4500);}
function badge(value) {const color=['CHECKED_IN','VIP','SUITE','ADVANCE'].includes(value)?'amber':['CANCELLED','HIDDEN','UNAVAILABLE'].includes(value)?'red':['CHECKED_OUT','MAINTENANCE','ANONYMOUS'].includes(value)?'gray':'';return `<span class="badge ${color}">${esc(title(value))}</span>`;}
function canWrite(module) {return state.user.role==='MANAGER' || ['guests','reservations'].includes(module);}
function shell(active) {
 $('#app').innerHTML=`<div class="shell"><aside class="sidebar">${brand}<div class="nav-label">WORKSPACE</div><nav class="nav" aria-label="Main navigation"><a href="#/dashboard" class="${active==='dashboard'?'active':''}">${icon('dashboard')}Overview</a>${Object.entries(modules).filter(([key])=>key!=='staff'||state.user.role==='MANAGER').map(([key,m])=>`<a href="#/${key}" class="${key===active?'active':''}">${icon(key)}${m.label}</a>`).join('')}</nav><div class="sidebar-foot"><div class="hotel-card"><strong>Serene · Colombo</strong><span><i class="status-dot"></i>Hotel operations workspace</span></div></div></aside><main class="main"><header class="topbar"><div class="breadcrumb"><button class="icon-button mobile-menu" aria-label="Toggle navigation">${icon('menu')}</button>Workspace <span>/</span><strong>${active==='dashboard'?'Overview':modules[active]?.label || 'Workspace'}</strong></div><div class="topright"><span class="date-label">${new Intl.DateTimeFormat('en-GB',{weekday:'short',day:'numeric',month:'short',year:'numeric'}).format(new Date())}</span><div class="avatar">${esc(state.user.name.split(' ').map(n=>n[0]).slice(0,2).join(''))}</div><div class="user-info">${esc(state.user.name)}<small>${title(state.user.role)}</small></div><button id="logout" class="icon-button" aria-label="Sign out" title="Sign out">${icon('logout')}</button></div></header><div class="content" id="content"><div class="loading">Loading workspace…</div></div></main></div>`;
 $('#logout').onclick=async()=>{try{await api('auth/logout',{method:'POST'});state.user=null;state.data={};await bootstrap();}catch(e){toast(e.message);}};
 $('.mobile-menu').onclick=()=>$('.sidebar').classList.toggle('open');
}
const footer = ()=>`<footer class="footer"><span>Serene Hotel & Residences · A little more considered.</span><span>Hotel reservation system / SE1020</span></footer>`;
const heading = (eyebrow,name,description,actions='')=>`<div class="page-head"><div><span class="eyebrow">${eyebrow}</span><h1>${name}</h1><p>${description}</p></div>${actions}</div>`;
function login() {
 $('#app').innerHTML=`<div class="login"><section class="login-art">${brand}<div class="circle"></div><div class="intro"><span class="eyebrow">THE ART OF A WARM WELCOME</span><h1>Good stays.<br>Great beginnings.</h1><p>A calmer way to manage your hotel. Every guest, every room, every little detail, beautifully in sync.</p></div><div class="bottom">COLOMBO, SRI LANKA &nbsp; / &nbsp; SERENE HOTEL & RESIDENCES</div></section><section class="login-form"><form id="login-form"><span class="eyebrow">YOUR HOTEL WORKSPACE</span><h1>Welcome back.</h1><p>Sign in to make today's stays extraordinary.</p><div id="login-error" role="alert"></div><div class="field"><label for="email">Email address</label><input id="email" type="email" name="email" autocomplete="username" placeholder="you@serene.local" required maxlength="160"></div><div class="field"><label for="password">Password</label><input id="password" type="password" name="password" autocomplete="current-password" placeholder="Enter your password" required maxlength="128"></div><button class="button" type="submit">Sign in to workspace ${icon('arrow')}</button><div class="login-note">Authorized hotel team members only.<br>Demo sign-in details are included in the project README.</div></form></section></div>`;
 $('#login-form').onsubmit=async event=>{event.preventDefault();const form=event.currentTarget,button=$('button',form);button.disabled=true;$('#login-error').innerHTML='';try{const result=await api('auth/login',{method:'POST',body:JSON.stringify(Object.fromEntries(new FormData(form)))});state.user=result.user;state.csrf=result.csrf;location.hash='#/dashboard';await route();}catch(e){$('#login-error').innerHTML=`<div class="error">${esc(e.message)}</div>`;}finally{button.disabled=false;}};
}
async function load(key) {state.data[key]=await api(key);return state.data[key];}
function guestName(id) {return state.data.guests?.find(g=>Number(g.id)===Number(id))?.name || `Guest #${id}`;}
function roomNumber(id) {return state.data.rooms?.find(r=>Number(r.id)===Number(id))?.number || id;}
function cell(key,value,row) {
 if(['type','status','role','shift','category'].includes(key)) return badge(value);
 if(['total','nightly_price','effective_cost'].includes(key)) return esc(money(value));
 if(key==='discount_rate') return `${Math.round(Number(value)*100)}%`;
 if(key==='guest_id') return esc(guestName(value));
 if(key==='room_id') return `Room ${esc(roomNumber(value))}`;
 if(key==='reservation_id') return value?`#SR-${String(value).padStart(4,'0')}`:'—';
 if(key==='id') return `#SR-${String(value).padStart(4,'0')}`;
 if(key==='rating') return `<span class="stars" aria-label="${esc(value)} out of 5">${'★'.repeat(Number(value))}${'☆'.repeat(5-Number(value))}</span>`;
 if(key==='text') return `<div class="detail-copy">${esc(value)}</div>`;
 if(key==='number') return `<strong>Room ${esc(value)}</strong>`;
 return esc(value);
}
function table(key,rows,withActions=true) {
 const m=modules[key];
 const columns=!withActions&&key==='reservations'?m.columns.filter(([k])=>['guest_id','room_id','check_in','status'].includes(k)):m.columns;
 if(!rows.length) return `<div class="empty">${icon(key)}<h3>No ${m.label.toLowerCase()} found</h3><p>Try another search${canWrite(key)?` or add your first ${m.singular}`:''}.</p>${canWrite(key)?`<a class="button" href="#/${key}/new">${icon('plus')}Add ${m.singular}</a>`:''}</div>`;
 return `<div class="table-wrap"><table><thead><tr>${columns.map(([,name])=>`<th scope="col">${name}</th>`).join('')}${withActions&&canWrite(key)?'<th scope="col">Actions</th>':''}</tr></thead><tbody>${rows.map(row=>`<tr>${columns.map(([field],i)=>`<td class="${i===0?'primary-cell':''}">${cell(field,row[field],row)}${field==='name'&&key==='guests'?`<small>Guest #${String(row.id).padStart(3,'0')}</small>`:''}</td>`).join('')}${withActions&&canWrite(key)?`<td><div class="table-actions"><a class="icon-button" href="#/${key}/${row.id}/edit" aria-label="Edit ${esc(m.singular)} ${row.id}" title="Edit">${icon('edit')}</a><button class="icon-button delete-record" data-module="${key}" data-id="${row.id}" aria-label="Delete ${esc(m.singular)} ${row.id}" title="Delete">${icon('trash')}</button></div></td>`:''}</tr>`).join('')}</tbody></table></div>`;
}
function bindDelete() {
 document.querySelectorAll('.delete-record').forEach(button=>button.onclick=()=>{
  const dialog=$('#confirm-dialog'),key=button.dataset.module,id=button.dataset.id;
  $('#confirm-title').textContent=`Delete this ${modules[key].singular}?`;
  $('#confirm-copy').textContent='This permanently removes the record. Linked records must be removed first. Active reservations must be cancelled or completed.';
  dialog.returnValue='';dialog.showModal();
  dialog.onclose=async()=>{if(dialog.returnValue==='delete'){try{await api(`${key}/${id}`,{method:'DELETE'});toast(`${title(modules[key].singular)} deleted.`);await route();}catch(e){toast(e.message);}}};
 });
}
async function dashboard(version) {
 const d=await api('dashboard');if(version!==state.routeVersion)return;
 state.data.guests=d.guestDirectory;state.data.rooms=d.roomInventory;
 const arrivals=d.reservations.filter(b=>b.check_in===d.today&&b.status==='CONFIRMED');
 const upcoming=d.reservations.filter(b=>b.status==='CONFIRMED').sort((a,b)=>a.check_in.localeCompare(b.check_in)).slice(0,5);
 const stat=(label,value,description,ico)=>`<div class="stat"><div class="stat-top">${label}<span class="stat-icon">${icon(ico)}</span></div><div class="stat-number">${value}</div><div class="stat-bottom">${description}</div></div>`;
 const occupied=new Set(d.reservations.filter(b=>b.status==='CHECKED_IN'&&b.check_in<=d.today&&b.check_out>d.today).map(b=>Number(b.room_id)));
 const greeting=new Date().getHours()<12?'Good morning':new Date().getHours()<17?'Good afternoon':'Good evening';
 $('#content').innerHTML=heading('A NEW DAY AT SERENE',`${greeting}, ${esc(state.user.name.split(' ')[0])}.`,`Here's what's happening at your hotel today.`,`<a href="#/reservations/new" class="button">${icon('plus')}New reservation</a>`)+`<div class="stats">${stat('Room occupancy',`${d.rooms?Math.round(d.occupied/d.rooms*100):0}<span style="font-size:18px">%</span>`,`<b>${d.occupied} occupied</b> of ${d.rooms} rooms`,'rooms')}${stat('Expected arrivals',String(d.arrivals).padStart(2,'0'),'Confirmed arrivals today','reservations')}${stat('Expected departures',String(d.departures).padStart(2,'0'),'Checked-in guests leaving today','logout')}${stat('Guest directory',String(d.guests).padStart(2,'0'),'Every guest, thoughtfully remembered','guests')}</div><div class="dashboard-grid"><div><section class="panel"><div class="panel-head"><div><h2>${arrivals.length?"Today's arrivals":"Upcoming reservations"}</h2><p>${arrivals.length?'A warm welcome is on the way.':'Your next stays, all in one place.'}</p></div><a class="text-link" href="#/reservations">View all ${icon('arrow')}</a></div>${table('reservations',arrivals.length?arrivals:upcoming,false)}</section><section class="panel"><div class="panel-head"><div><h2>Room overview</h2><p>A live snapshot of the hotel floor.</p></div><a class="text-link" href="#/rooms">Manage rooms ${icon('arrow')}</a></div><div class="room-visual">${d.roomInventory.map(r=>{const status=r.status==='MAINTENANCE'?'maintenance':occupied.has(Number(r.id))?'occupied':'available';return `<a href="#/rooms${canWrite('rooms')?`/${r.id}/edit`:''}" class="room-tile ${status}" aria-label="Room ${esc(r.number)}, ${status}">${icon('rooms')}<strong>${esc(r.number)}</strong><span>${title(status)}</span></a>`;}).join('')}</div><div class="legend"><span><i></i>Available now</span><span><i class="amber"></i>Occupied</span><span><i class="gray"></i>Maintenance</span></div><p class="help" style="padding:0 24px 20px">Future availability is checked against your reservation dates when saving.</p></section></div><aside class="dashboard-aside"><section class="banner"><span class="eyebrow">HOSPITALITY, CONSIDERED.</span><h2>Every detail.<br>Every guest. Every day.</h2><p>A little care goes a long way. Let's make this a stay to remember.</p></section><section class="panel"><div class="panel-head"><div><h2>Recent activity</h2><p>The latest changes in your workspace.</p></div></div><div class="activity">${d.activity.length?d.activity.map(line=>{const split=line.indexOf(' ');return `<div class="activity-item"><i class="dot"></i><div>${esc(line.substring(split+1))}<small>${esc(new Date(line.substring(0,split)).toLocaleString('en-GB'))}</small></div></div>`;}).join(''):'<p>No changes yet. Your team’s activity will appear here.</p>'}</div></section></aside></div>`+footer();
}
async function listing(key,version) {
 const m=modules[key];const rows=await load(key);
 if(key==='reservations') await Promise.all([load('guests'),load('rooms')]);
 if(version!==state.routeVersion)return;
 $('#content').innerHTML=heading('HOTEL OPERATIONS',m.label,m.description,`<div class="head-actions">${state.user.role==='MANAGER'?`<button class="button secondary" id="export">${icon('download')}Export .txt</button>`:''}${canWrite(key)?`<a class="button" href="#/${key}/new">${icon('plus')}Add ${m.singular}</a>`:''}</div>`)+`<section class="panel"><div class="panel-head"><div><h2>${m.label==='Team'?'Team directory':m.label+' directory'}</h2><p>${rows.length} records in your workspace</p></div></div><div class="toolbar"><div class="search">${icon('search')}<label class="visually-hidden" for="search">Search ${m.label}</label><input id="search" type="search" placeholder="Search ${m.label.toLowerCase()}…"></div><label class="visually-hidden" for="filter">Filter by ${title(m.filter)}</label><select id="filter"><option value="">All ${title(m.filter).toLowerCase()}s</option>${[...new Set(rows.map(r=>r[m.filter]))].sort().map(value=>`<option value="${esc(value)}">${title(value)}</option>`).join('')}</select></div><div id="table-container">${table(key,rows)}</div><div class="count-label" id="count">Showing ${rows.length} of ${rows.length} records</div></section>`+footer();
 function filter() {const query=$('#search').value.toLowerCase(),value=$('#filter').value;const filtered=rows.filter(r=>(!value||r[m.filter]===value)&&[...Object.values(r),key==='reservations'?guestName(r.guest_id):'',key==='reservations'?roomNumber(r.room_id):''].join(' ').toLowerCase().includes(query));$('#table-container').innerHTML=table(key,filtered);$('#count').textContent=`Showing ${filtered.length} of ${rows.length} records`;bindDelete();}
 $('#search').oninput=filter;$('#filter').onchange=filter;bindDelete();
 if($('#export'))$('#export').onclick=async()=>{const button=$('#export');button.disabled=true;try{const response=await fetch(`/api/${key}/export`,{credentials:'same-origin'});if(!response.ok){const data=await response.json();throw new Error(data.error);}const url=URL.createObjectURL(await response.blob());const a=document.createElement('a');a.href=url;a.download=`${key}.txt`;a.click();setTimeout(()=>URL.revokeObjectURL(url),1000);toast('Text snapshot exported.');}catch(e){toast(e.message);}finally{button.disabled=false;}};
}
const defaults = {guests:{type:'STANDARD',discount_rate:0},rooms:{type:'STANDARD',capacity:2,base_price:18000,luxury_tax:0,status:'AVAILABLE'},reservations:{type:'ADVANCE',party_size:1,status:'CONFIRMED'},staff:{role:'RECEPTIONIST',shift:'MORNING'},services:{category:'DINING',type:'PAID',base_cost:0,status:'ACTIVE'},reviews:{type:'ANONYMOUS',rating:5,status:'PUBLISHED',author:'Anonymous'}};
function fieldMarkup(key,spec,value,editing) {
 const [name,label,type,options,help]=spec;const fieldId='field-'+name;
 let choices=options?.map(value=>[value,title(value)]);
 if(type==='guest') choices=state.data.guests.map(g=>[g.id,`${g.name} · ${title(g.type)}`]);
 if(type==='room') choices=state.data.rooms.map(r=>[r.id,`Room ${r.number} · ${title(r.type)} · ${money(r.nightly_price)} / night · ${r.capacity} guests${r.status==='MAINTENANCE'?' · Maintenance':''}`]);
 if(type==='booking') choices=state.data.reservations.filter(b=>b.status==='CHECKED_OUT').map(b=>[b.id,`#SR-${String(b.id).padStart(4,'0')} · ${guestName(b.guest_id)} · ${b.check_out}`]);
 const optional=(name==='password'&&editing)||name==='reservation_id';
 let control;
 if(choices)control=`<select id="${fieldId}" name="${name}" ${!optional?'required':''}><option value="">Select ${label.toLowerCase()}</option>${choices.map(([v,l])=>`<option value="${esc(v)}" ${String(v)===String(value)?'selected':''}>${esc(l)}</option>`).join('')}</select>`;
 else if(type==='textarea')control=`<textarea id="${fieldId}" name="${name}" required maxlength="${name==='text'?2000:1000}" rows="4">${esc(value)}</textarea>`;
 else {let constraints='';if(type==='number'){const range={discount_rate:[0,.5,.01],luxury_tax:[0,.3,.01],capacity:[1,12,1],party_size:[1,12,1],base_price:[.01,10000000,.01],base_cost:[0,10000000,.01]}[name];if(range)constraints=`min="${range[0]}" max="${range[1]}" step="${range[2]}"`;}const max={name:100,email:160,phone:30,number:12,password:128,author:100}[name]||200;control=`<input id="${fieldId}" name="${name}" type="${type}" value="${type==='password'?'':esc(value)}" ${!optional?'required':''} maxlength="${max}" ${type==='password'?'minlength="10" autocomplete="new-password"':''} ${constraints}>`;}
 return `<div class="field ${type==='textarea'?'wide':''}"><label for="${fieldId}">${label}${optional?' <span style="font-weight:400;color:var(--muted)">(optional)</span>':''}</label>${control}${help?`<div class="help">${help}</div>`:''}</div>`;
}
async function editor(key,id,version) {
 const m=modules[key];if(!canWrite(key))throw new Error('Manager access is required to change this module.');
 if(['reservations','reviews'].includes(key))await Promise.all([load('guests'),load('rooms'),load('reservations')]);
 const record=id?await api(`${key}/${id}`):{...defaults[key]};if(version!==state.routeVersion)return;
 $('#content').innerHTML=heading(id?'UPDATE RECORD':'CREATE RECORD',`${id?'Edit':'Add'} ${m.singular}`,id?`Update the details for ${m.singular} #${id}.`:`Add a new ${m.singular} to your workspace.`,`<a href="#/${key}" class="button secondary">${icon('back')}Back to ${m.label.toLowerCase()}</a>`)+`<section class="panel form-panel"><div class="panel-head"><div><h2>${title(m.singular)} details</h2><p>Complete the information below and review before saving.</p></div>${icon(key)}</div><form id="record-form" class="form-body"><div id="form-error" role="alert"></div>${key==='reservations'?'<div class="notice">Rates include suite tax and guest discounts. Advance booking cancellations carry a 10% fee; walk-in cancellations carry no fee. Cancel or complete an active booking before deleting it.</div>':''}<div class="form-grid">${m.fields.map(spec=>fieldMarkup(key,spec,record[spec[0]]??'',Boolean(id))).join('')}${key==='reservations'?'<div class="price-preview" id="price-preview" aria-live="polite">Choose a guest, room, and stay dates to estimate the total.</div>':''}</div><div class="form-actions"><a href="#/${key}" class="button secondary">Cancel</a><button class="button" type="submit">${icon('check')}${id?'Save changes':'Create '+m.singular}</button></div></form></section>`+footer();
 const form=$('#record-form');
 function dependencies() {
  if(key==='reviews'){const verified=form.elements.type.value==='VERIFIED';form.elements.reservation_id.required=verified;form.elements.reservation_id.disabled=!verified;form.elements.author.disabled=!verified;if(!verified)form.elements.author.value='Anonymous';}
  if(key==='guests'){form.elements.discount_rate.disabled=form.elements.type.value!=='VIP';if(form.elements.discount_rate.disabled)form.elements.discount_rate.value=0;}
  if(key==='rooms'){form.elements.luxury_tax.disabled=form.elements.type.value!=='SUITE';if(form.elements.luxury_tax.disabled)form.elements.luxury_tax.value=0;}
  if(key==='services'){form.elements.base_cost.disabled=form.elements.type.value==='COMPLIMENTARY';if(form.elements.base_cost.disabled)form.elements.base_cost.value=0;}
  if(key==='reservations'){
   const v=Object.fromEntries(new FormData(form)),room=state.data.rooms.find(r=>String(r.id)===v.room_id),guest=state.data.guests.find(g=>String(g.id)===v.guest_id),nights=(Date.parse(v.check_out)-Date.parse(v.check_in))/86400000;
   if(room&&guest&&nights>0){const same=id&&['guest_id','room_id','check_in','check_out','type'].every(k=>String(record[k])===String(v[k]));const total=same?Number(record.total):room.nightly_price*nights*(1-guest.discount_rate);$('#price-preview').innerHTML=`${nights} night${nights===1?'':'s'} · Room ${esc(room.number)} · ${esc(title(guest.type))} guest <strong>${esc(money(total))}</strong><br><span class="help">${same?'Existing agreed price retained.':'Estimate; the server validates availability and calculates the final total.'}${v.status==='CANCELLED'?` Cancellation fee: ${money(v.type==='ADVANCE'?total*.1:0)}.`:''}</span>`;}else $('#price-preview').textContent='Choose a guest, room, and valid stay dates to estimate the total.';
  }
 }
 form.onchange=dependencies;form.oninput=()=>{if(key==='reservations')dependencies();};dependencies();
 form.onsubmit=async event=>{event.preventDefault();const button=$('button[type=submit]',form);button.disabled=true;$('#form-error').innerHTML='';const values=Object.fromEntries(new FormData(form));for(const control of form.elements)if(control.disabled&&control.name)values[control.name]=control.value;try{await api(key+(id?`/${id}`:''),{method:id?'PUT':'POST',body:JSON.stringify(values)});toast(`${title(m.singular)} ${id?'updated':'created'} successfully.`);location.hash=`#/${key}`;}catch(e){$('#form-error').innerHTML=`<div class="error">${esc(e.message)}</div>`;$('#form-error').scrollIntoView({behavior:'smooth',block:'center'});}finally{button.disabled=false;}};
}
async function route() {
 if(!state.user){login();return;}
 const version=++state.routeVersion;const parts=(location.hash.slice(2)||'dashboard').split('/');const key=parts[0];
 if(key!=='dashboard'&&!modules[key]){location.hash='#/dashboard';return;}
 shell(key);
 try {
  if(key==='staff'&&state.user.role!=='MANAGER')throw new Error('Manager access is required to view the team directory.');
  if(key==='dashboard')await dashboard(version);
  else if(parts[1]==='new')await editor(key,null,version);
  else if(parts[2]==='edit'&&/^\d+$/.test(parts[1]))await editor(key,parts[1],version);
  else await listing(key,version);
 }catch(e){if(version===state.routeVersion&&state.user)$('#content').innerHTML=`<div class="error">${esc(e.message)}</div><a href="#/dashboard" class="button secondary">Return to overview</a>`;}
}
async function bootstrap() {try{const session=await api('auth/session');state.csrf=session.csrf;state.user=session.user||null;await route();}catch(e){$('#app').innerHTML=`<div class="empty"><h1>Unable to connect</h1><p>${esc(e.message)}</p><button class="button" id="retry">Try again</button></div>`;$('#retry').onclick=bootstrap;}}
window.addEventListener('hashchange',route);
bootstrap();
