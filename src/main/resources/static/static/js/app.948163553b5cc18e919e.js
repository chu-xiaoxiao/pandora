webpackJsonp([1],{"4Uwr":function(e,t,a){e.exports=a.p+"static/img/logo.f5b1ee8.jpg"},NHnr:function(e,t,a){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var s=a("7+uW"),l={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{attrs:{id:"app"}},[e._m(0),e._v(" "),a("div",{staticClass:"medium"},[a("el-col",{staticClass:"tac"},[a("el-menu",{staticClass:"menu",attrs:{"background-color":"#545c64","text-color":"#fff","active-text-color":"#ffd04b"}},[a("el-submenu",{attrs:{index:"1"}},[a("template",{slot:"title"},[a("i",{staticClass:"el-icon-location"}),e._v(" "),a("span",[e._v("数据模版")])]),e._v(" "),a("el-menu-item",{attrs:{index:"1-1"}},[a("router-link",{attrs:{to:"/db"}},[e._v("数据模版生成")])],1)],2),e._v(" "),a("el-submenu",{attrs:{index:"2"}},[a("template",{slot:"title"},[a("i",{staticClass:"el-icon-menu"}),e._v(" "),a("span",[e._v("文件操作")])]),e._v(" "),a("el-menu-item",{attrs:{index:"2-1"}},[a("router-link",{attrs:{to:"/file"}},[e._v("文件上传")])],1)],2)],1)],1),e._v(" "),a("div",{staticClass:"reight"},[a("router-view")],1)],1)])},staticRenderFns:[function(){var e=this.$createElement,t=this._self._c||e;return t("div",{staticClass:"top"},[t("img",{staticClass:"img",attrs:{src:a("4Uwr")}})])}]};var r=a("VU/8")({name:"App"},l,!1,function(e){a("ekgm")},null,null).exports,o=a("/ocq"),n={name:"db",data:function(){return{dbParam:{dbName:null,baseModelName:null,user:null,password:null,tableNames:null,url:null}}},methods:{success:function(e,t){e&&this.$message({showClose:!0,message:e,type:"success",duration:t||3e3})},fail:function(e){e&&this.$message({showClose:!0,message:e,type:"warning"})},download:function(e){if(e){var t=window.URL.createObjectURL(new Blob([e])),a=document.createElement("a");a.style.display="none",a.href=t,a.setAttribute("download","DbGenerator.zip"),document.body.appendChild(a),a.click()}},generator:function(){var e=this;this.axios.get("/DB/generator",{params:e.dbParam,responseType:"blob"}).then(function(t){console.log(t),e.download(t.data)})}}},i={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{attrs:{name:"db"}},[a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:10}},[a("el-form",{staticClass:"form",attrs:{"label-width":"200px",model:e.dbParam}},[a("el-form-item",{attrs:{label:"数据库IP:端口"}},[a("el-input",{model:{value:e.dbParam.url,callback:function(t){e.$set(e.dbParam,"url",t)},expression:"dbParam.url"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"数据库名"}},[a("el-input",{model:{value:e.dbParam.dbName,callback:function(t){e.$set(e.dbParam,"dbName",t)},expression:"dbParam.dbName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"数据库用户"}},[a("el-input",{model:{value:e.dbParam.user,callback:function(t){e.$set(e.dbParam,"user",t)},expression:"dbParam.user"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"数据库密码"}},[a("el-input",{model:{value:e.dbParam.password,callback:function(t){e.$set(e.dbParam,"password",t)},expression:"dbParam.password"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"基础包名"}},[a("el-input",{model:{value:e.dbParam.baseModelName,callback:function(t){e.$set(e.dbParam,"baseModelName",t)},expression:"dbParam.baseModelName"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"表明 多个|分隔"}},[a("el-input",{model:{value:e.dbParam.tableNames,callback:function(t){e.$set(e.dbParam,"tableNames",t)},expression:"dbParam.tableNames"}})],1),e._v(" "),a("el-button",{staticClass:"but",attrs:{type:"primary"},on:{click:e.generator}},[e._v("生成代码")])],1)],1)],1)],1)},staticRenderFns:[]};var c=a("VU/8")(n,i,!1,function(e){a("s2Uo")},"data-v-2dfd4eaa",null).exports,u={name:"file",data:function(){return{startRequestParam:{url:null,branch:null,project:null,rootProject:null,checkHealthUrl:null},logParam:{type:null,offset:null},log:null,logTimer:null}},methods:{success:function(e,t){e&&this.$message({showClose:!0,message:e,type:"success",duration:t||3e3})},fail:function(e){e&&this.$message({showClose:!0,message:e,type:"warning"})},deploy:function(){var e=this;this.axios.post("/deploy",this.startRequestParam).then(function(t){0==t.data.code?(e.success(t.data.message),e.startLog()):e.error(t.data.message),console.log(t)}).catch(function(t){console.log(t),e.error(response.data.message)})},rangeLog:function(){var e=this;this.axios.get("/log",{params:e.logParam}).then(function(t){if(e.log+=t.data.data.data,void 0==t.data.data.offset||t.data.data.offset<0)e.logParam.offset=0;else{e.logParam.offset=t.data.data.offset;var a=document.getElementById("logDiv");console.log(a.scrollTop,a.scrollHeight),a.scrollTop=a.scrollHeight}1==t.data.data.over&&clearInterval(e.timer)}).catch(function(e){console.log(e)})},startLog:function(){this.logParam.offset=0,this.logParam.type="packageLog",this.logParam.project=this.startRequestParam.project,this.timer=setInterval(this.rangeLog,3e3)}}},m={render:function(){var e=this,t=e.$createElement,a=e._self._c||t;return a("div",{attrs:{name:"file"}},[a("el-row",{attrs:{gutter:20}},[a("el-col",{attrs:{span:10}},[a("el-form",{staticClass:"form",attrs:{"label-width":"200px",model:e.startRequestParam,"label-position":"right"}},[a("el-form-item",{attrs:{label:"项目地址"}},[a("el-input",{model:{value:e.startRequestParam.url,callback:function(t){e.$set(e.startRequestParam,"url",t)},expression:"startRequestParam.url"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"分支"}},[a("el-input",{model:{value:e.startRequestParam.branch,callback:function(t){e.$set(e.startRequestParam,"branch",t)},expression:"startRequestParam.branch"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"应用"}},[a("el-input",{model:{value:e.startRequestParam.project,callback:function(t){e.$set(e.startRequestParam,"project",t)},expression:"startRequestParam.project"}})],1),e._v(" "),a("el-form-item",{attrs:{label:"是否根路径运行"}},[a("el-switch",{model:{value:e.startRequestParam.rootProject,callback:function(t){e.$set(e.startRequestParam,"rootProject",t)},expression:"startRequestParam.rootProject"}})],1),e._v(" "),a("el-button",{staticClass:"but",attrs:{type:"primary"},on:{click:e.deploy}},[e._v("发布")])],1)],1),e._v(" "),a("el-col",{attrs:{span:10}},[a("div",{staticStyle:{"overflow-y":"auto","overflow-x":"auto",height:"600px"},attrs:{id:"logDiv"},domProps:{innerHTML:e._s(e.log)}})])],1)],1)},staticRenderFns:[]};var d=a("VU/8")(u,m,!1,function(e){a("vcBo")},"data-v-39d4ce78",null).exports;s.default.use(o.a);var f=new o.a({routes:[{path:"/",name:""},{path:"/db",name:"db",component:c},{path:"/file",name:"file",component:d}]}),p=a("zL8q"),b=a.n(p),v=a("mtWM"),g=a.n(v),h=a("Rf8U"),P=a.n(h);a("tvR6");s.default.config.productionTip=!1,s.default.use(b.a),s.default.use(P.a,g.a),new s.default({el:"#app",router:f,components:{App:r},template:"<App/>"})},ekgm:function(e,t){},s2Uo:function(e,t){},tvR6:function(e,t){},vcBo:function(e,t){}},["NHnr"]);
//# sourceMappingURL=app.948163553b5cc18e919e.js.map