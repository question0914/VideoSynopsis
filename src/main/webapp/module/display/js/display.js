$('#display').bootstrapTable({
    url: '/video/files',
    columns: [{
        field: 'fileName',
        title: 'File Name'
    }, {
        field: 'modifyDate',
        title: 'Modify Date'
    }, {
        field: 'downloadLink',
        title: 'Download Link'
    }]
});

// private String fileName;
// private String modifyDate;
// private String downloadLink;