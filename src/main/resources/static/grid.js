var columnDefs = [
    {headerName: 'ID', field: 'id', flex: 1},
    {headerName: 'STATUS', field: 'status', flex : 1, cellStyle: function(params) {
        if(params.value=='Finished') {
            return {backgroundColor: 'green'}
        } 
        if(params.value=='Failed') {
            return {backgroundColor: 'orange'}
        }
        else {
            return null
        }
    }},
    {headerName: 'NAME', field: 'name', flex: 2},
    {headerName: 'TIME STARTED', field: 'timeStarted', flex: 2, sort: 'desc'},
    {headerName: 'TIME FINISHED', field: 'timeFinished', flex: 2},
];

var gridOptions = {
    columnDefs: columnDefs,
    rowSelection: 'multiple',
    onRowDoubleClicked: onRowDoubleClicked,
    pagination: true,
    defaultColDef: {
        sortable: true,
        resizable: true,
        filter: true,
    },
};

function onRowDoubleClicked(event) {
    //delete old results grid if it exists
    var allGrids = document.getElementsByClassName("ag-root-wrapper ag-layout-normal ag-ltr");
    if(allGrids.length > 1) {
        var oldResultsGrid = allGrids[1]
        oldResultsGrid.parentNode.removeChild(oldResultsGrid)
    }
    //using table data (name + time started) to find the correct results file
    var step1 = event.node.data.timeStarted
    var step2 = step1.toString()
    var step3 = step2.replace(/-/g, "")
    var step4 = step3.replace("20", "-")
    var step5 = step4.replace(/:/g, "")
    var niceTimeStarted = step5.replace(" ", "-")
    var resultFolder = event.node.data.name + '.mopt' + niceTimeStarted
    $.ajax({
        method: 'POST',
        url: "/getresults",
        data: JSON.stringify(resultFolder),
        success: function(resultsJson) {
            var columnsIn = resultsJson.columns
            var columnDefs2 = []
            var ii = 0
            for(var key in columnsIn){
                var headerName = columnsIn[key]
                var field = ii.toString()
                columnDefs2.push({headerName, field})
                ii++
            }
            console.log(columnDefs2)
            console.log(resultsJson.rows)
            var gridOptions2 = {
                columnDefs:  columnDefs2,
                enableRangeSelection: true,
                enableValue: true,
                rowSelection: 'multiple',
                defaultColDef: {
                    sortable: true,
                    resizable: true,
                    filter: true,
                    flex: 1,
                },
                statusBar: {
                    statusPanels: [
                      { statusPanel: 'agFilteredRowCountComponent' },
                      { statusPanel: 'agSelectedRowCountComponent' },
                      { statusPanel: 'agAggregationComponent' },
                    ],
                  },
            }
            var rows = resultsJson.rows
            var gridRows = []
            var i
            while(rows.length > 0) {
                for(i = 0; i < columnsIn.length; i++) {
                    var row = rows.slice(0, columnsIn.length)
                    var rowGrid = Object.assign({}, row)
                    gridRows.push(rowGrid)
                    for(i = 0; i < columnsIn.length; i++) {
                        rows.shift()
                    }
                }
            }
            var eGridDiv2 = document.querySelector('#resultsGrid');
            new agGrid.Grid(eGridDiv2, gridOptions2);
            gridOptions2.api.setRowData(gridRows)
        }
    })
}

var eGridDiv = document.querySelector('#jobGrid');

new agGrid.Grid(eGridDiv, gridOptions);

agGrid.simpleHttpRequest({
    url: '/getjobs'
})
    .then(function(data) {
        gridOptions.api.setRowData(data);
        console.log(data)
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