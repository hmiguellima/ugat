function DatePicker(parent) {
     this.dt=new Date();
     this.days=null;
     this.parent=parent;
     this.oSpan=document.createElement('div');
     this.oSpan.style.cssText='position:absolute;left:0px;top:0px;visibility:hidden;';
     this.oSpan.className='DatePicker';
     this.parent.appendChild(this.oSpan);
     this.render();
}

DatePicker.prototype.show = function(dt, callback, days, monthYearCallback) {
     if (dt) this.dt=dt;
     this.days=days;
     this.callback=callback;
     this.monthYearCallback=monthYearCallback;
     this.fill();
     this.oSpan.style.visibility="visible";
     this.oMonth.focus();
}

DatePicker.prototype.hide = function() {
	this.parent.removeChild(this.oSpan);
}

DatePicker.prototype.render = function() {
    var oT1, oTR1, oTD1, oTH1;
    var oT2, oTR2, oTD2;

    this.oSpan.appendChild(oT1 = document.createElement("table"));
    oT1.className = 'DatePickerTable';
    oT1.border = 0;

    oTR1 = oT1.insertRow(oT1.rows.length);
    oTD1 = oTR1.insertCell(oTR1.cells.length);
    oTD1.colSpan = 7;

		this.monthBar=oTD1;
    oTD1.className = 'DatePickerHdr';
    oT2 = document.createElement("table");
    oTD1.appendChild(oT2);
    oT2.border = 0;

    oTR2 = oT2.insertRow(oT2.rows.length);
    oTD2 = oTR2.insertCell(oTR2.cells.length);
    oTD2.title = this.texts.prevMonth;
    oTD2.onclick = function() { this.oDatePicker.onPrev(); }
    oTD2.oDatePicker = this;
    oTD2.innerHTML = "&lt;&lt;";
    oTD2.className = 'DatePickerHdrBtn';

    oTD2 = oTR2.insertCell(oTR2.cells.length);
    this.oMonth = document.createElement("span");
    this.oMonth.className = 'DatePickerHdrLabel';
    oTD2.appendChild(this.oMonth);
    this.oMonth.oDatePicker = this;
    /*this.oMonth.onchange = this.oMonth.onkeyup =
        function() { this.oDatePicker.onMonth(); }
		for ( var i = 0; i < 12; i++ ) {
		   this.oMonth.add(new Option(this.texts.months[i], i),undefined);
		}*/

    this.oYear = oTR2.insertCell(oTR2.cells.length);
    this.oYear.title = this.texts.yearTitle;
    this.oYear.oDatePicker = this;
    this.oYear.onclick = function() { this.oDatePicker.onYear(); }
    this.oYear.className = 'DatePickerHdrBtn';

    oTD2 = oTR2.insertCell(oTR2.cells.length);
    oTD2.title = this.texts.nextMonth;
    oTD2.onclick = function() { this.oDatePicker.onNext(); }
    oTD2.oDatePicker = this;
    oTD2.innerHTML = "&gt;&gt;";
    oTD2.className = 'DatePickerHdrBtn';

    oTR1 = oT1.insertRow(oT1.rows.length);
    for ( i = 0; i < 7; i++ ) {
        oTH1 = document.createElement("th");
        oTR1.appendChild(oTH1);
        oTH1.innerHTML = this.texts.days[i];
        oTH1.className = 'DatePicker';
    }


     this.aCells = new Array;
     for ( var j = 0; j < 6; j++ ) {
        this.aCells.push(new Array);
        oTR1 = oT1.insertRow(oT1.rows.length);
        for ( i = 0; i < 7; i++ ) {
           this.aCells[j][i] = oTR1.insertCell(oTR1.cells.length);
           this.aCells[j][i].className='DatePickerBtn';
           this.aCells[j][i].oDatePicker = this;
           this.aCells[j][i].onclick =
              function() { this.oDatePicker.onDay(this); }
	        }
     }
}

DatePicker.prototype.fill = function() {
    // first clear all
    this.clear();

    // place the dates in the calendar
    var nRow = 0;
    var d = new Date(this.dt.getTime());
    var m = d.getMonth();
    for ( d.setDate(1); d.getMonth() == m; d.setTime(d.getTime() + 86400000) ) {
       var nCol = d.getDay();
       this.aCells[nRow][nCol].innerHTML = d.getDate();

       if (this.days) {
         for (index=0;index<this.days.length;index++) {
           if (this.days[index]==d.getDate()) {
	           this.aCells[nRow][nCol].className = 'DatePickerBtnSelect';
	           break;
	         }
	       }
       } else
	       if ( d.getDate() == this.dt.getDate() ) {
	          this.aCells[nRow][nCol].className = 'DatePickerBtnSelect';
	       }
       if ( nCol == 6 ) nRow++;
    }

    // set the month combo
    //this.oMonth.value = m;
    this.oMonth.innerHTML = this.texts.months[m] + " / ";

    // set the year text

    this.oYear.innerHTML = this.dt.getFullYear();
}
 

DatePicker.prototype.clear = function() {
    for ( var j = 0; j < 6; j++ )
       for ( var i = 0; i < 7; i++ ) {
          this.aCells[j][i].innerHTML = "&nbsp;"
          this.aCells[j][i].className = 'DatePickerBtn';
       }
}

DatePicker.prototype.onPrev = function() {
    this.dt.setDate(0);
    if (this.monthYearCallback) 
      this.monthYearCallback(this.dt);
    this.fill();
}
 

DatePicker.prototype.onNext = function() {
    this.dt.setDate(1);
    this.dt.setMonth(this.dt.getMonth()+1);
    if (this.monthYearCallback) 
      this.monthYearCallback(this.dt);
    this.fill();
}

DatePicker.prototype.onMonth = function() {
    this.dt.setDate(1);
    this.dt.setMonth(this.oMonth.value);
    if (this.monthYearCallback) 
      this.monthYearCallback(this.dt);
    this.fill();
}

DatePicker.prototype.onYear = function() {
    var y = parseInt(prompt(this.texts.yearQuestion, this.dt.getFullYear()));
    if ( !isNaN(y) ) {
       this.dt.setDate(1);
       this.dt.setFullYear(parseInt(y));
       if (this.monthYearCallback) 
         this.monthYearCallback(this.dt);
       this.fill();
    }
}

DatePicker.prototype.onDay = function(oCell) {
    var d = parseInt(oCell.innerHTML);
    if ( d > 0 ) {
       this.dt.setDate(d);
       this.callback(this.dt);
    }
}

DatePicker.prototype.hideMonthBar = function() {
    this.monthBar.style.display="none";
}

DatePicker.prototype.showMonthBar = function() {
    this.monthBar.style.display="block";
}

DatePicker.prototype.texts = {
     months: [
       "Janeiro", "Fevereiro", "Marco",
       "Abril", "Maio", "Junho",
       "Julho", "Agosto", "Setembro",
       "Outubro", "Novembro", "Dezembro"
    ],
    days: ["Dom", "Seg", "Ter", "Qua", "Qui", "Sex", "Sab"],
    prevMonth: "Mes anterior",
    nextMonth: "Proximo mes",
    yearTitle: "Clique para alterar",
    yearQuestion: "Novo ano:"
};

DatePicker.prototype.selectDays = function(days) {
    this.days=days.split(",");
    this.fill();
}

DatePicker.prototype.getElement = function() {
  return this.oSpan;
}
