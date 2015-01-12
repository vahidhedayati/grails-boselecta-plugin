<input type="text" id="${id}" onChange="updatePlaceHolder(this);updateList(this.value, '${id}', '${sDataList}', '${setId}')"
  list="${dataList}" name="${name}" placeholder="${placeHolder ?: 'AutoComplete' }">
<datalist id="${dataList}"></datalist>
<script>
function updatePlaceHolder(value) {
	//console.log(value.name+" "+value.value);
	 //input.placeholder = "";
}
</script>>

