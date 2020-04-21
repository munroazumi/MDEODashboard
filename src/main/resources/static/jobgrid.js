var columnDefs = [
    {headerName: 'ID', field: 'id', sortable: true, resizable: true, flex: 1},
    {headerName: 'STATUS', field: 'status', sortable: true, resizable: true, flex : 1, cellStyle: function(params) {
        if (params.value=='Finished') {
            return {backgroundColor: 'green'};
        } else {
            return null;
        }
    }},
    {headerName: 'NAME', field: 'name', sortable: true, resizable: true, flex: 2},
    {headerName: 'TIME STARTED', field: 'timeStarted', sortable: true, resizable: true, flex: 2},
    {headerName: 'TIME FINISHED', field: 'timeFinished', sortable: true, resizable: true, flex: 2},
];

var gridOptions = {
    columnDefs: columnDefs,
    rowSelection: 'single',
    onRowSelected: onRowSelected,
    onSelectionChanged: onSelectionChanged,
};

function onSelectionChanged() {
    var selectedRows = gridOptions.api.getSelectedRows();
    document.querySelector('#selectedRows').innerHTML = selectedRows.length === 1 ? selectedRows[0].athlete : '';
}

function onRowSelected(event) {
    var step1 = event.node.data.timeStarted
    var step2 = step1.toString()
    var step3 = step2.replace(/-/g, "")
    var step4 = step3.replace("20", "-")
    var step5 = step4.replace(/:/g, "")
    var niceTimeFinished = step5.replace(" ", "-")
    var resultFolder = event.node.data.name + '.mopt' + niceTimeFinished
    $.ajax({
        method: 'POST',
        url: "/getresults",
        data: JSON.stringify(resultFolder),
        success: function() {window.alert(resultFolder)}
    })
}

var eGridDiv = document.querySelector('#jobGrid');

new agGrid.Grid(eGridDiv, gridOptions);

agGrid.simpleHttpRequest({
    url: '/getjobs'
})
    .then(function(data) {
        gridOptions.api.setRowData(data);
    })

function refreshGrid() {
    console.log('calling refresh')
    agGrid.simpleHttpRequest({
        url: '/getjobs'
    })
        .then(function(data) {
            gridOptions.api.setRowData(data);
        })
}