// import React from 'react';
// import {PageHeaderWrapper} from "@ant-design/pro-layout";
// import {Button, Card, Collapse, Input, List, Table, Typography} from 'antd';
//
// const { Panel } = Collapse;
//
//
// const Component = () => {
//     const data1 = [
//         'GD202008200001',
//         'GD202008200001',
//         '还有2项......',
//     ];
//
//     const list = ['1','2','3','4','5','6','7'];
//     const columns = [
//         {
//             title: '设备状态',
//             dataIndex: 'status',
//             key: 'status',
//         },
//         {
//             title: '设备名称',
//             dataIndex: 'name',
//             key: 'name',
//         },
//         {
//             title: list[0]+' 星期一',
//             dataIndex: '0',
//             key: '0',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[1]+' 星期二',
//             dataIndex: '1',
//             key: '1',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[2]+' 星期三',
//             dataIndex: '2',
//             key: '2',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[3]+' 星期四',
//             dataIndex: '3',
//             key: '3',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[4]+' 星期五',
//             dataIndex: '4',
//             key: '4',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[5]+' 星期六',
//             dataIndex: '5',
//             key: '5',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//         {
//             title: list[6]+' 星期日',
//             dataIndex: '6',
//             key: '6',
//             render: text => (
//                 <div >
//
//                     <List
//                         bordered={false}
//                         dataSource={data1}
//                         renderItem={item => (
//                             <List.Item >
//                                 <a >{item}</a>
//                                 <Typography.Text mark></Typography.Text>
//                             </List.Item>
//                         )}
//                     />
//                     <Button type='primary'>分配</Button>
//                     <Button type='primary' style={{marginLeft:8}}>分配1</Button>
//
//                 </div>
//
//             )
//         },
//
//
//     ];
//
//     const data = [
//         {
//             status:'正常',
//             name:'切割机'
//         },
//         {
//             status:'正常',
//             name:'焊接机'
//         },
//         {
//             status:'预警',
//             name:'机床'
//         },
//     ];
//
//
//     return (
//         // @ts-ignore
//         <PageHeaderWrapper
//             className="site-page-header"
//             title="大标题"
//             subTitle="标题的描述"
//         >
//             <div>
//                 <Card>
//
//                     <Collapse defaultActiveKey={['1']} >
//                         <Panel header="搜索区域" key="1">
//                             <Input addonBefore='条件1' style={{width:'20%',marginLeft:80}}></Input>
//                             <Input addonBefore='条件2' style={{width:'20%',marginLeft:16}}></Input>
//                             <Input addonBefore='条件3' style={{width:'20%',marginLeft:16}}></Input>
//                             <Button type='primary' style={{marginLeft:120}}>搜索</Button>
//                             <Button type='primary' style={{marginLeft:8}}>重置</Button>
//                         </Panel>
//                     </Collapse>
//
//
//                     <Table columns={columns} dataSource={data} bordered={true} />
//
//                 </Card>
//
//             </div>
//         </PageHeaderWrapper>
//     )
// };
//
// export default Component;
